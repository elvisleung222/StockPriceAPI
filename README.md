# Get Started
1. Pull the project and source code from repository
```sh
git clone https://github.com/elvisleung222/StockPriceAPI.git
cd StockPriceAPI
```
2. Run docker images in container
```sh
docker-compose up -d
```
3. Wait until all containers are up and ready
4. Check if all services are connected to Eureka server
```sh
User:StockPriceApplication home$ docker ps
CONTAINER ID        IMAGE                                                 COMMAND                  CREATED             STATUS              PORTS                    NAMES
02c319c11fea        elvisleung222/stockprice-eureka-feign-client:v1.0.2   "java -jar /app.jar"     28 minutes ago      Up 28 minutes       0.0.0.0:8000->8000/tcp   stockpriceapplication_stockprice-eureka-feign-client_1
c19d8392ddc5        elvisleung222/stockprice-api:v1.0.1                   "java -jar /app.jar"     28 minutes ago      Up 28 minutes       0.0.0.0:8001->8001/tcp   stockpriceapplication_stock-price-app-8001_1
8f861699bce3        elvisleung222/stockprice-api:v1.0.1                   "java -jar /app.jar"     28 minutes ago      Up 28 minutes       0.0.0.0:8002->8002/tcp   stockpriceapplication_stock-price-app-8002_1
d51464465019        mysql:8.0                                             "docker-entrypoint.s…"   28 minutes ago      Up 28 minutes       3306/tcp, 33060/tcp      stockpriceapplication_stock-price-db_1
e9b4ec67a2d7        elvisleung222/stockprice-eureka-server:initial        "java -jar /app.jar"     28 minutes ago      Up 28 minutes       0.0.0.0:8761->8761/tcp   stockpriceapplication_stockprice-eureka-server_1
```
5. Checkout the Eureka dashboard: http://localhost:8761/

Instances currently registered with Eureka

|Application|AMIs|Availability Zones|Status|
|-----------|----|------------------|------|
|EUREKA-FEIGN-CLIENT|n/a (1)|(1)|UP (1) - eureka-feign-client:8000|
|STOCK-PRICE-SERVICE|n/a (2)|(2)|UP (2) - stock-price-service:8002 , stock-price-service:8001|

(_You will find three services are registered in Eureka discovery registry. In this setup, two stock-price-service instances are up for load balancing._)

6. Test the Eureka load balancing endpoint: http://localhost:8000/health/

Try to call this endpoint mutiple times. Then, you will see messages from TWO instances randomly with port numbers (8001 / 8002).
```
Instance "stock-price-service:8001" is running......
Instance "stock-price-service:8002" is running......
Instance "stock-price-service:8002" is running......
Instance "stock-price-service:8001" is running......
```

7. Checkout the Swagger of APIs: http://localhost:8001/swagger-ui.html
8. Run the sample Python script to insert 20 samples stock with 5 days prices data 

_(It sends API requests to Eureka Feign Client load balancer)_
```sh
# Python version 3 only
python StockPriceAPI/script/importData.py
# OR
python3 StockPriceAPI/script/importData.py
```
**Note: If you receive 500 response, please check if all instances (docker containers) are connected to the Eureka server. Otherwise, you might need to wait a couple more minutes for re-sync and try again.**

# Highlights
## Stock Price API
- Project path: `./StockPriceAPI/`
- Functionalities
    - Post request: post/update multiple symbols and their corresponding historical price into database
    - Get request: query historical price by symbols and time range
    - Delete request: delete symbol and its corresponding data by symbols
- Sample GET request: `http://localhost:8000/prices?symbols=AAPL, NIO, TSLA&from=2020-01-01&to=2020-08-01`
- Please refer to the Swagger for API usage in detail
## Swagger
- URL: http://localhost:8001/swagger-ui.html
## Multi-threaded Part
- *ExecutorService* is used with a fixed thread pool for execution
```java
ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS)
```
- *ExecutorCompletionService* acts like a execution queue to handle tasks in parallel and receive deliverables from all threads.
```java
final List<StockPriceDTO> stockPriceDTOList = new ArrayList<>();
final CompletionService<StockPriceDTO> completionService = new ExecutorCompletionService<>(executor);

// Split tasks and handle in different threads
for (String symbol : symbolList) {
    completionService.submit(() -> {
        List<Price> prices = priceService.getPrices(symbol, from, to);
        StockPriceDTO dto = StockPriceDTO.builder().symbol(symbol).prices(prices).build();
        return dto;
    });
}

// Collect result from threads
for (String symbol : symbolList) {
  StockPriceDTO resultDto = completionService.take().get();
  stockPriceDTOList.add(resultDto);
}
```
- Please refer to `/StockPriceAPI/src/main/java/com/stockprice/controller/StockPriceController.java`
## Dockerization
- Three applications are dockerized
- Dockerfile is located in each project directory: `./StockPriceAPI/`, `./EurekaFeignClient/` and `./EurekaServer/`
- docker-compose is located in root directory: `./`
- Images of all these three applications are published to Docker hub
  - https://hub.docker.com/r/elvisleung222/stockprice-api/tags
  - https://hub.docker.com/r/elvisleung222/stockprice-eureka-feign-client/tags
  - https://hub.docker.com/r/elvisleung222/stockprice-eureka-server/tags
- Images are pulled and used in the `./docker-compose.yml`
## Eureka Load Balancing
- Eureka Server (./EurekaServer/) serves as a discovry registry for service.
- Eureka Discovery Client (./EurekaFeignClient/) serves as a load balancer to route requests to different instances.
