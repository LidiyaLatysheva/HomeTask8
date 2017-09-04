:: Beginning of hub batch file
set SERVER_VERSION=3.5.3
set TASK_NAME=SeleniumServerNode3
set NODE_PORT=5557
set HUB_PORT=4444
set REGISTER_IP=localhost
set CHROME_DRIVER=d:\webdrivers\chromedriver.exe
java -Dwebdriver.chrome.driver=%CHROME_DRIVER% -jar selenium-server-standalone-%SERVER_VERSION%.jar -role node -hub http://%REGISTER_IP%:%HUB_PORT%/grid/register -browser "browserName=chrome,version=58,maxInstances=5,platform=WINDOWS" -port %NODE_PORT%
:: End of hub batch file
pause