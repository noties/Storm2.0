call gradlew.bat annotations:buildUpload query:buildUpload database:buildUpload iterator:buildUpload lazy:buildUpload types:buildUpload reflect:buildUpload --configure-on-demand
call gradlew.bat cursor-mock:buildUpload --configure-on-demand
call gradlew.bat stormparser:parser-runtime:buildUpload stormparser:parser-apt:buildUpload --configure-on-demand
call gradlew.bat core:buildUpload --configure-on-demand
call gradlew.bat rx:buildUpload --configure-on-demand
call gradlew.bat runtime:buildUpload --configure-on-demand