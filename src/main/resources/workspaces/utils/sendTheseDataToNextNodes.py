def sendTheseDataToNextNodes (**kwargs):
    import pickle
    if kwargs is not None:
        print("#BEGINNING OF DATA RETRIEVING")
        for key, value in kwargs.items():
            print("key ", key)
            print("pickle ", pickle.dumps(value))
            print("json ", jsonify(value))

def jsonify (value):
    import json
    #print(value)
    if type(value) in (int, float, complex, bool, str, bytes):
        return json.dumps(value)
    elif type(value).__module__ == 'numpy':
        return json.dumps(value.tolist())
    elif isinstance(value, list):
        res = '['
        for member in value:
            res += jsonify(member) + ','
        res += res[:-1] + ']'
        return res
    elif isinstance(value, dict):
        res = '{'
        for key, item in value.items():
            res += '"' + key + '": ' + jsonify(item) + ','
        res = res[:-1] + '}'
        return res
    else :
        print(jsonify(value.__dict__))
        return jsonify(value.__dict__)