def sendTheseDataToNextNodes (**kwargs):
    if kwargs is not None:
        for key, value in kwargs.iteritems():
            print("#BEGINNING OF DATA RETRIEVING")
            print("key ", key)
            print(value)