.PHONY: build run stop clean

IMAGE_NAME = sarvasya-lms-backend
CONTAINER_NAME = sarvasya-lms-app

# Build the docker image
build:
	docker build -t $(IMAGE_NAME) .

# Run the docker-compose stack (App + DB)
run:
	docker-compose up --build -d

# Stop the docker-compose stack
stop:
	docker-compose down

# Clean docker volumes
clean:
	docker-compose down -v
