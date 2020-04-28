#!/usr/bin/env bash

today=`date +'%m-%d-%Y'`

curl -s https://raw.githubusercontent.com/borkdude/babashka/master/install -o install-babashka
chmod +x install-babashka && sudo ./install-babashka

curl -v -s "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/$today.csv" -o today.csv

bb csv_to_jsons.clj
