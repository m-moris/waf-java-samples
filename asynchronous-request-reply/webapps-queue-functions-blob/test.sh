#!/bin/bash

echo "REQUEST  http://localhost:10080/api/post"
MES="Asynchronous Request Reply Pattern Sample" 
ID=$(curl -s -d "message=${MES}" http://localhost:10080/api/post | jq -r .id)
echo -e "REQUEST ID = ${ID}\n\n"

for i in {0..5}; do
    echo "POLLING http://localhost:10080/api/state/${ID}"
    curl -i -L http://localhost:10080/api/state/${ID}
    sleep 2
done
