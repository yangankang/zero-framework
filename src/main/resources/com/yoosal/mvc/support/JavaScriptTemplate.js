var defaultSuffixName = "JController";

function executor(cinfo) {
    if (typeof cinfo == 'string') {
        cinfo = eval("(" + cinfo + ")");
    }
    if (cinfo['suffixName']) {
        defaultSuffixName = cinfo['suffixName'];
    }
    var codeArray = [],
        controllers = cinfo['cunits'];
    codeArray.push("var " + defaultSuffixName + "=" + defaultSuffixName + "||{}");
    codeArray.push(defaultSuffixName + ".address=\"\"");
    codeArray.push(__mvc_ajax_object.toString());
    codeArray.push(__$assign.toString());
    for (var i in controllers) {
        var controllerName = i,
            cunits = controllers[i];
        codeArray.push(defaultSuffixName + "." + controllerName + "={}");
        for (var j in cunits) {
            var method = cunits[j]['controllerMethodParse'],
                methodName = method['methodName'],
                javaMethodParamNames = method['javaMethodParamNames'],
                paramString = cunits[j]['paramString'],
                url = cunits[j]['url'],
                json = cunits[j]['paramObject'];

            var funString = templateFunction.toString()
                .replace("_params_", paramString)
                .replace("_url_", url)
                .replace("_json_", json)
                .replace("_address_", defaultSuffixName + ".address")

                .replace("_address_", defaultSuffixName + ".address")
                .replace("_url_", url)
                .replace("_json_", json);

            codeArray.push(defaultSuffixName + "." + controllerName + "." + methodName + "=" + funString);
        }
    }
    var codeString = "";
    for (var i in codeArray) {
        codeString += codeArray[i] + "\r\n";
    }
    return codeString;
}

var templateFunction = function (_params_) {
    var _$object = new Object();
    _$object.add = function (key, value) {
        if (!this._params) this._params = {};
        this._params[key] = value;
        return this;
    };
    _$object.setPage = function (page) {
        if (!this._params) this._params = {};
        this._params['_page'] = page;
        return this;
    };
    _$object.call = function (SuccessFunction, FailedFunction) {
        var _$json = __$assign(_json_, this._params);
        __mvc_ajax_object("POST", _address_ + "_url_", _$json, SuccessFunction, FailedFunction);
    };
    _$object.info = function () {
        var _$json = __$assign(_json_, this._params);
        return {url: _address_ + "_url_", param: _$json};
    };
    return _$object;
};

function __$assign(a, b) {
    if (!a) a = {};
    if (b) {
        for (var i in b) {
            a[i] = b[i];
        }
    }
    return a;
}

function __mvc_ajax_object(type, url, data, success, failed) {
    var self = new Object();
    self.success = success;
    self.failed = failed;
    self.dataToRequest = function (data) {
        if (!data)return null;
        var dataStr = "";
        for (var i in data) {
            var d = data[i];
            if (d == null)continue;
            if (typeof d == 'object') {
                d = JSON.stringify(d);
            }
            dataStr = dataStr + "&" + i + "=" + encodeURIComponent(d);
        }
        if (!dataStr || dataStr == "")return null;
        return dataStr.substring(1, dataStr.length);
    };

    try {
        self.XMLHttpReq = new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
        try {
            self.XMLHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
        } catch (e) {
            self.XMLHttpReq = new XMLHttpRequest();
        }
    }
    self.XMLHttpReq.open(type, url, true);
    self.XMLHttpReq.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
    self.XMLHttpReq.onreadystatechange = function () {
        if (self.XMLHttpReq.readyState == 4) {
            var text = self.XMLHttpReq.responseText;
            if (self.XMLHttpReq.status == 200) {
                if (self.success) {
                    try {
                        text = JSON.parse(text);
                    } catch (e) {
                    }
                    try {
                        self.success(text);
                    } catch (e) {
                        throw e;
                    }
                }
            } else {
                if (self.failed) {
                    self.failed(self.XMLHttpReq.status);
                }
            }
        }
    };
    self.XMLHttpReq.send(self.dataToRequest(data));
}

