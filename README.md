# order-management-system

Whenever an order place button is clicked -> 
1. Fetch the price quotes
2. Verify if there has been a price update 
3. If the prices match
    1. Create the order 
    2. Call payments service to pay in an async manner 
    3. Expose an API to payments [Improvement possible :  use queue] to change the payment status
    4. Payments will call my REST API -> with the success or failure Response 
    5. If failed then create an order with payment status Failed and place COD order if wanted -> depends on project requirements
    6. If successful, update the payment status
4. Else show required failure response


RESILIENCY
Circuit Breaker : Resilience4j 
Why resilience4j - It is lightweight as compared to its counterparts
Circuit breaker is used when the pricing API has not responded even after 3 retries.
We can add rate limiting as well on our APIs for Internet security, as DoS attacks can tank a server with unlimited API requests.


SCALABILITY
Will be vertically scaling data store , i.e. more CPU, more memory or more storage
high Availability on data store  will be maintained by using replication 

scalability as per data growth will be kept ---> Sharding could be done eventually either horizontally or vertically
If traffic grows , we can eventually use a master slave model.

