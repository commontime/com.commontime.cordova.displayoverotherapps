/*global cordova, module*/

module.exports = {
	 enabled: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "DisplayOverOtherApps", "enabled", []);
    }
};