#!/usr/bin/env bash

mkdir -p src/main/webapp
cd src/main/webapp/
rm -rf ./*
git clone https://github.com/antoinecheron/padins-ui .
npm install
npm run build
cd ../../../
mvn package
