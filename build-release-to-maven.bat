call gradlew.bat annotations:uploadArchives query:uploadArchives database:uploadArchives iterator:uploadArchives lazy:uploadArchives types:uploadArchives reflect:uploadArchives --configure-on-demand -Prelease_to_maven=1
call gradlew.bat cursor-mock:uploadArchives --configure-on-demand -Prelease_to_maven=1
call gradlew.bat stormparser:parser-runtime:uploadArchives stormparser:parser-apt:uploadArchives --configure-on-demand -Prelease_to_maven=1
call gradlew.bat core:uploadArchives --configure-on-demand -Prelease_to_maven=1
call gradlew.bat rx:uploadArchives --configure-on-demand -Prelease_to_maven=1
call gradlew.bat runtime:uploadArchives --configure-on-demand -Prelease_to_maven=1