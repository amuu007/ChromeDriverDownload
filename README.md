# ChromeDriverDownload
Author : Amruta Alandkar 
Email: amrutasa007@gmail.com
This program provides the compatible chromedriver.exe with the installed chrome version.
Google chrome autoupdates every biweekly or so notoriously and your test script using chromedriver.exe might fail randomly with incompatible version error. 
Run this script before your selenium scripts in Jenkins/CI/CD pipeline to always get the right chromedriver.exe file.

This program takes two arguments - 
1)ChromeDriver Download location (Here a zip file will be downloaded from the web)
2)chromedriver.exe unzip location (This can be the location from where your selenium project is picking the chromedriver.exe, so that it picks the compatible chromedriver.exe every time)

Argumnets example:
C:\Users\admin\Downloads\ChromeDriverDownload C:\jenkins\workspace\SeleniumProject\Chromedriver

This script creates folders if not available and overwrites the existing folders/files to write new ones.
