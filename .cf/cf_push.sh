cf push -f rabbitmq-producer-test.yml --vars-file ./vars.yml

cf bind-service rabbitmq-producer-test tasy-rabbitmq --binding-name rabbitmq-producer-test-tasy-rabbitmq