#!/usr/bin/env bash

lein clean
rm -rf release
lein cljsbuild once frontend-release
lein cljsbuild once electron-release
gulp electron