#!/bin/bash

(cd back && docker-compose -f docker-compose-auth-service.yml up --build) & \
(cd back && docker-compose -f docker-compose-stock-service.yml up --build)