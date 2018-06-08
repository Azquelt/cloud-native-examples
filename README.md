# Cloud Native Java Example

To run the demo:
* Place `openliberty.zip` in this directory
* Open a terminal, change to the `serviceA` directory and run `mvn package liberty:run-server`
* Open another terminal, change to the `serviceC` directory and run `mvn package liberty:run-server`
* Go to http://localhost:9080/serviceA/props/service.name to check everything's working
* Download and extract [prometheus](https://prometheus.io/download/)
* Place `prometheus.yml` from this directory into your prometheus install directory
  * This file includes configuration to have prometheus extract metrics from the liberty servers
* In a new terminal, run `./prometheus` from the prometheus install directory
  * The prometheus console should now be available at http://localhost:9090
* Open VS Code and and add the `serviceA` and `serviceC` directories to your workspace
  * They should build successfully and liberty should restart the app
* At this point you can start hacking about with the code
  * Open `ServiceARest.java` and change the call to `getPropertyEasy()` to one of the other `getProperty...` calls
  * Liberty should pick up the changes and restart the app automatically