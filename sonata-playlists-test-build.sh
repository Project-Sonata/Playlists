#!/bin/bash

export IMAGE_NAME=sonata-playlists

echo "Starting building image with name: $IMAGE_NAME"

docker image build -f Dockerfile.test -t $IMAGE_NAME .

echo "Image $IMAGE_NAME has been successfully built"