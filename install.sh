#!/usr/bin/env bash

cd src/main/webapp/
rm -rf ./*
git clone https://github.com/antoinecheron/padins-ui .
npm install
npm run build
cd ../../../
mvn package
docker pull antoinecheronirisa/lmt-python-core