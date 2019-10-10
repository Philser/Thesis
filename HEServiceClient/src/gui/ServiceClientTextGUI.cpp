#include "ServiceClientTextGUI.h"

ServiceClientTextGUI::ServiceClientTextGUI(ServiceClient* service_client) {
            this->service_client = service_client;
}

    
void ServiceClientTextGUI::print_UI(string target, string peer_id) {
    cout << "************ SAP HE Service Client ************" << endl;
    cout << "Selected service: " << target << endl;
    cout << "Peer ID: " << peer_id << endl;
    cout << "                      " << endl;
    cout << "(1) Set target service" << endl;
    cout << "(2) Choose run" << endl;
    cout << "(3) Create run" << endl;
    cout << "(4) Insert value" << endl;
    cout << "(5) Get result" << endl;
    cout << "(q) Exit" << endl;
    cout << "***********************************************" << endl;
}

void ServiceClientTextGUI::interact() {
    ServiceClient client = this->get_client();
    string clear_screen_sequence = "\033[2J\033[1;1H";
    string service_url = "";
    string peer_id = "";
    while(true) {
        service_url = client.get_target_service();
        peer_id = client.get_peer_id();
        print_UI(service_url, peer_id);
        flush(cout);
        string action;
        getline(cin, action);
        cout << clear_screen_sequence;

        if(action.compare("1") == 0) {
            try {
                string chosen_service = this->prompt_services();
                client.set_service(chosen_service);
                cout << "New service url: " << client.get_target_service() << endl;
            }
            catch(GenericException e) {
                cout << e.what() << endl;
            }           
        }
        else if(action.compare("2") == 0) {
            try{
                string run_id = prompt_run();
                client.set_run(run_id);
            }
            catch(GenericException e) {
                cout << e.what() << endl;
            }
        }
        else if(action.compare("3") == 0) {
            cout << "Create run for peer group: ";
            string pg_id = "";
            getline(cin, pg_id);
            cout << endl;
            try {
                string run_id = client.create_run(pg_id);
                client.set_run(run_id);
            }
            catch(GenericException e) {
                cout << e.what() << endl;
            }       
        }
        else if(action.compare("4") == 0) {
            string variable_name;
            long variable_value;
            cout << "Variable name: ";
            getline(cin, variable_name);
            cout << "Variable value: ";
            cin >> variable_value;
            try{
                client.upload_value(variable_name, variable_value);
                cout << "Value uploaded successfully" << endl;
            }
            catch(GenericException e) {
                cout << e.what() << endl;
            }    
        }
        else if(action.compare("5") == 0) {
            try{
                long result = client.fetch_result();
                cout << "Computation result: " << result << endl;
            }
            catch(GenericException e) {
                cout << e.what() << endl;
            }   
        }
        else if(action.compare("q") == 0) {
            return void();
        }
    }
}

string ServiceClientTextGUI::prompt_run() {
    cout << "Prompting runs .. " << endl;
    vector<vector<string>> runs = this->service_client->get_runs(); //Output all runs 
    cout << "Fetched runs" << endl;
    string run = "";
    if(runs.size() == 0) {
        cout << "No runs available. Please create one." << endl;
    }
    else {
        while(true) {
            int run_no;
            cout << "Choose a run: ";
            cin >> run_no;
            run = to_string(run_no);
            if(this->run_is_valid(runs, run))
                break;
            cout << "Invalid run." << endl;
        }
    }

    return run;
}

string ServiceClientTextGUI::prompt_services() {
    cout << "Fetching services ..." << endl;
   vector<vector<string>> services = this->get_client().fetch_available_services();
   cout << "Fetched services" << endl;
   if(services.size() > 0) {
       string chosen_service = "";
       while(true) {
            cout << "Available services: " << endl << endl;
            for(int i = 0; i < services.size(); i++) {
                vector<string> service = services.at(i);
                cout << service.at(0) << ":   " << service.at(1) << "" << endl;
            }
            cout << endl;
            cout << "Choose service: " ;
            getline(cin, chosen_service);
            if(service_is_valid(services, chosen_service))
                break;
            cout << "Invalid service" << endl;
       }       
        return chosen_service;
   }
   else {
       cout << "No services available." << endl;
       return "";
   }
}

bool ServiceClientTextGUI::run_is_valid(vector<vector<string>> runs, string given_run) {
    return this->service_is_valid(runs, given_run);
}

bool ServiceClientTextGUI::service_is_valid(vector<vector<string>> services, string given_service) {
    for(int i = 0; i < services.size(); i++) {
        vector<string> service = services.at(i);
        if(service.at(0) == given_service)
            return true;
    }
    return false;
}

ServiceClient ServiceClientTextGUI::get_client() {
    cout << "Returning client ... " << endl;
    return (*this->service_client);
}