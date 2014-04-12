#!/bin/sh

curl \
--header "Content-type: application/json" \
--request POST \
--data '{"username": "alvin", "password": "alvin"}' \
http://localhost:8080/login


