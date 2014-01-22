#!/bin/bash

hg serve --config web.push_ssl=No --config "web.allow_push=*"
