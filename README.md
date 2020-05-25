# i3connect
This connector enables easy access to JSON Data from BMW Connected API for simple HTTP clients. 
Typical use case is the collection, processing and display of BMW i3 battery electric vehicle data in a dashboard style statistics platform.
Given examples provide a docker deployment which integrates Telegraf, InfluxDB and Grafana to provide such solution.

## Feature Overview
* Takes care of API token handling 
* Includes caching to avoid rate limitation issues
* Provides aggregated and flattened data model, convenient to consume with Influxdb Telegraf inputs.httpjson Plugin
* Supports pass-through of data from essential API functions efficiency, dynamic, navigation and chargingprofile
* Can be deployed as docker container

## Usage
* User, password and vehicle identification number have to be specified as HTTP headers - see [examples/example-requests.http](./examples/example-requests.http)
* Docker Image can be built from included [Dockerfile](./Dockerfile)

### Example environment with Telegraf, InfluxDB and Grafana
1. Customize username, password and VIN headers in "inputs.httpjson" section of Telegraf configuration [examples/telegraf.conf](./examples/telegraf.conf). (It also includes a inputs.http section template for Tesla Powerwall users)
2. Example environment can now be started with Docker-Compose using [examples/docker-compose.yml](./examples/docker-compose.yml). (Please make sure that the image name for the connector matches. Chronograf and Kapacitor are optional and can be removed)
3. Grafana should now be accessible via HTTP on port 3000 of your Docker host. Feel free to import the example dashboard "i3 Stats" from [examples/grafana-dashboard.json](./examples/grafana-dashboard.json)