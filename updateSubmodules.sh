#!/bin/bash
echo 'Updating submodules...'
git submodule update --init
git submodule foreach git submodule update --init
echo 'Finished updating submodules'