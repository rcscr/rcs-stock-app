#!/bin/bash

cd back && docker-compose -f docker-compose-stock-service-it-tests.yml up --build --remove-orphans --abort-on-container-exit
