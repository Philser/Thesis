#include "ServiceClient.h"

ServiceClient::ServiceClient(string service_base_url, string peer_id, string context_file_location, string secret_key_file_location) {
    cout << "Reading context from file " << context_file_location << endl;

    this->base_service_url = service_base_url;
    this->peer_id = peer_id;

    unsigned long p = 2;
    unsigned long r = 16;
    unsigned long m = 0;
    vector<long> gens;
    vector<long> ords;

	ifstream context_file;
	context_file.open(context_file_location);
	readContextBase(context_file, m, p, r, gens, ords);
	FHEcontext context(m, p, r, gens, ords);
	context_file >> context;
	context_file.close();
	cout << "Done." << endl;

    FHESecKey secKey = this->read_secret_key_from_file(&context, secret_key_file_location);
    FHEPubKey& pubKey = secKey;
    secretKey = &secKey;
    publicKey = &pubKey;
}

FHEPubKey ServiceClient::get_pub_key() {
    return (*this->publicKey);
}

FHESecKey ServiceClient::get_sec_key() {
    return (*this->secretKey);
}

string ServiceClient::get_peer_id() {
    return this->peer_id;
}

string ServiceClient::get_target_service() {
    return this->base_service_url + this->service_name + this->run_id;
}

string ServiceClient::get_central_service() {
    return this->base_service_url + this->central_endpoint;
}

void ServiceClient::set_run(string run_id) {
    this->run_id = run_id;
}

void ServiceClient::convert_string_to_ctxt(string value, Ctxt& ctxt) {
    stringstream ss(value);
    ss >> ctxt;
    ss.flush();
}

void ServiceClient::set_service(string service_name) {
    this->service_name = service_name + "/";
}

void ServiceClient::convert_ctxt_to_string(Ctxt& ctxt, string& target_string) {
	cout << "Converting ctxt to string ... " << endl;
    stringstream ss;
    ss << ctxt;
    target_string = ss.str();
    ss.flush();
    cout << "Done." << endl;
}

long ServiceClient::decrypt_result(FHESecKey secKey, Ctxt result) {
	ZZX value_result;
	secKey.Decrypt(value_result, result);

	long plain_result;
	conv(plain_result, value_result[0]);

    return plain_result;
}

bool ServiceClient::run_is_valid(string run) {
    vector<vector<string>> runs = this->get_runs();
    for(int i = 0; i < runs.size(); i++) {
        if(runs.at(i).at(0) == run) //compare runIds
            return true;
    }
    return false;
}

vector<vector<string>> ServiceClient::get_runs() {
    cout << "Getting runs.." << endl;
    string target = this->get_target_service();
    cout << "Target: " << target << endl;
    http_client client(U(target));
    http_request request(methods::GET);

    vector<vector<string>> runs = {};

    http_response response = client.request(request).get();

    if(response.status_code() == 500) {
        throw new GenericException(response.extract_string().get());
    }
    
    json::value json_response = response.extract_json().get();  
    if(json_response.size() > 0 && json_response.has_field("runs")){
        auto runs_array = json_response.at("runs").as_array();        
        for(int i = 0; i < runs_array.size(); i++) {
            vector<string> run = {};
            if(runs_array.at(i).has_field("runId")) {
                string runId = runs_array.at(i).at("runId").as_string();
                run.push_back(runId);
            }
            if(runs_array.at(i).has_field("peerGroupId")) {
                string peerGroupId = runs_array.at(i).at("peerGroupId").as_string();
                run.push_back(peerGroupId);
            }
            runs.push_back(run);
        }
        return runs;                      
    } else {
        string response = json_response.serialize();
        throw new GenericException(response);
    }
    
    return runs;
}

string ServiceClient::create_run(string peer_group_id) {
    json::value json_obj;
    json_obj["peerGroupId"] = json::value::string(peer_group_id);
    string target = this->get_target_service();

    http_client client(U(target + "createRun/"));
    http_request request(methods::POST);
    request.set_body(json_obj);

    string run_id = "";

    http_response response = client.request(request).get();

    if(response.status_code() == 500) {
        throw new GenericException(response.extract_string().get());
    }

    json::value json_response = response.extract_json().get();

    if(json_response.size() > 0 && json_response.has_field("status")){
        string status = json_response.at("status").as_string();
        if(status == "created") {
            run_id = json_response.at("runId").as_string();        
        }
        else if(status == "error") {
            string error_name = json_response.at("errorName").as_string();
            string error_message = json_response.at("errorMessage").as_string();
            throw GenericException("Received error: " + error_name + ": " + error_message);
        }     
    } else {
            throw GenericException("Could not read response: " + json_response.serialize());
    }

    return run_id;
}

void ServiceClient::upload_value(string var_name, long var_value) {
    FHEPubKey pubKey = this->get_pub_key();
    Ctxt c1(pubKey);
    pubKey.Encrypt(c1, to_ZZX(var_value));
    string ctxt_string = "";
    
    this->convert_ctxt_to_string(c1, ctxt_string);
    this->upload_value_to_service(var_name, ctxt_string);
}

void ServiceClient::upload_value_to_service(string variable_name, string variable_value) {
    json::value json_obj;
    json_obj["varName"] = json::value::string(variable_name);
    json_obj["varValue"] = json::value::string(variable_value);
    json_obj["peerId"] = json::value::string(peer_id);
    
    string target = this->get_target_service();
    http_client client(U(target));
    http_request request(methods::POST);
    request.set_body(json_obj);

    http_response response = client.request(request).get();

    if(response.status_code() == 500) {
        throw new GenericException(response.extract_string().get());
    }

    json::value json_response = response.extract_json().get();
    
    if(json_response.size() > 0){
        if(json_response.has_field("status")) {
            string status = json_response.at("status").as_string();
            if(status == "error") {
                string error_name = json_response.at("errorName").as_string();
                string error_message = json_response.at("errorMessage").as_string();
                throw GenericException("Received error: " + error_name + ": " + error_message);
            }
        }
    } else {
        throw GenericException("Error: Empty response.");
    }
}

long ServiceClient::fetch_result() {
    string target = this->get_target_service();
    http_client client(U(target));
    http_request request(methods::GET);

    string ctxt_string = "";

    http_response response = client.request(request).get();

    if(response.status_code() == 500) {
        throw new GenericException(response.extract_string().get());
    }

    json::value json_response = response.extract_json().get();
    FHEPubKey pubKey = this->get_pub_key();
    long result = 0;
    if(json_response.size() > 0){
        if(json_response.has_field("status")) {
            string status = json_response.at("status").as_string();
            if(status == "Done") {              
                Ctxt result_ctxt(pubKey);
                ctxt_string = json_response.at("result").as_string();
                convert_string_to_ctxt(ctxt_string, result_ctxt);
                FHESecKey secKey = this->get_sec_key();
                result = decrypt_result(secKey, result_ctxt);
            }
            else if(status == "Started") {
                throw GenericException("Computation has been started. Please wait");
            }
            else {
                throw GenericException("Computation is still in progress.");
            }
        }
        else if(json_response.has_field("missingVariables")) {
            json::array missing_vars_array = json_response.at("missingVariables").as_array();
            string message = "Cannot start computation. Values still missing:\n";
            for(json::array::iterator it = missing_vars_array.begin(); it < missing_vars_array.end(); it++) {
                message += (*it).as_string() + "\n";
            }
            throw GenericException(message);
        }
    } else {
        throw GenericException("Error reading result");
    }

    return result;
}

vector<vector<string>> ServiceClient::fetch_available_services() {
    cout << "Client: Fetching services" << endl;
    vector<vector<string>> services;
    string target = this->get_central_service();
    cout << "Target is: " << target << endl;
    http_client client(U(target));
    http_request request(methods::GET);

    cout << "Sending request ... ";
    pplx::task<http_response> resp_task = client.request(request);
    while(!resp_task.is_done()) {

    }
    resp_task
    .then([](http_response response) {
        return response.extract_json();
    })
    .then([&](json::value json_response) {
        cout << "Reading json ... " << endl;
        if(json_response.size() > 0){
            if(json_response.has_field("services")) {
                json::array services_array = json_response.at("services").as_array();
                for(json::array::iterator it = services_array.begin(); it < services_array.end(); it++) {
                    vector<string> service;
                    cout << "Pushing to service" << endl;
                    json::object service_obj = (*it).as_object();
                    service.push_back(service_obj.at("name").as_string());
                    service.push_back(service_obj.at("function").as_string());
                    cout << "Pushing to services" << endl;
                    services.push_back(service);
                }
            }
        } else {
            throw new GenericException("Error: Empty response.");
        }
    });

    resp_task.wait();
    
    return services;
}

FHESecKey ServiceClient::read_secret_key_from_file(FHEcontext* context, string secret_key_file_location) {
    cout << "Reading secret key from " << secret_key_file_location << "... " << endl;
	ifstream context_file;
	context_file.open(secret_key_file_location);
	FHESecKey secKey((*context));
	context_file >> secKey;
	context_file.close();
	cout << "Done." << endl;

	return secKey;
}