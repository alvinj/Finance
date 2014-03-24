#!/bin/sh

#curl \
#--request POST \
#--data "id=0&symbol=TSLA&companyName=Tesla" \
#http://localhost:8080/stocks/add

#--data '{"id": 0, "symbol": "TSLA", "companyName" : "Tesla"}' \

curl \
--header "Content-type: application/json" \
--request POST \
--data '{"symbol": "TSLA", "companyName" : "Tesla"}' \
http://localhost:8080/stocks/add


