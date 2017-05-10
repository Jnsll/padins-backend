#!/usr/bin/env bash
# $1 must be the container id
docker inspect $1 | grep IPAddress | cut -d '"' -f 4