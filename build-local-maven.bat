call gradlew.bat annotations:buildUpload query:buildUpload database:buildUpload iterator:buildUpload lazy:buildUpload types:buildUpload serializer:buildUpload reflect:buildUpload --configure-on-demand
call gradlew.bat stormscheme:scheme-runtime:buildUpload stormscheme:scheme-apt:buildUpload --configure-on-demand
call gradlew.bat cursor-mock:buildUpload --configure-on-demand
call gradlew.bat stormparser:parser-runtime:buildUpload stormparser:parser-apt:buildUpload --configure-on-demand
call gradlew.bat core:buildUpload --configure-on-demand
call gradlew.bat rx:buildUpload --configure-on-demand

echo Press any key to exit...
pause >nul
exit