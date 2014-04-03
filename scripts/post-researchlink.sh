#!/bin/sh

curl \
--header "Content-type: application/json" \
--request POST \
--data '{"symbol": "TSLA", "url": "http://finance.yahoo.com/news/best-selling-car-norway-tesla-113037330.html", "notes": "Norway loves Tesla"}' \
http://localhost:8080/research_links/add


