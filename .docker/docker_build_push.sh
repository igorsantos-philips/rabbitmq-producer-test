cd ../
docker login docker.sa1.hsdp.io -u $CF_DOCKER_USER -p $CF_DOCKER_PASSWORD
docker build . -t rabbitmq-producer-test && \
docker tag rabbitmq-producer-test docker.sa1.hsdp.io/hsdp-tasy/rabbitmq-producer-test && \
docker push docker.sa1.hsdp.io/hsdp-tasy/rabbitmq-producer-test
