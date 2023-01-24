# Knoldus AWS Library Example Application

## How to run on local?
1. Create a new `.env-knoldus-aws-sample-app` file
2. Copy the environment variables from [.env-knoldus-aws-sample-app-example](.env-knoldus-aws-sample-app-example) file and paste them in the above file.
3. Start the localstack service inside a docker container: 
    ```
    sudo docker-compose up
    ```
4. Run the application:
    ```
    sbt run
    ``` 