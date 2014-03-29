#!/bin/sh

curl \
--header "Content-type: application/json" \
--request POST \
--data '{"symbol": "TSLA", "ttype": "B", "quantity": 100, "price": 200.50, "notes": "seems like a good buy"}' \
http://localhost:8080/transactions/add


