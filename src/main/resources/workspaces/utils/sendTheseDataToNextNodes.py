def sendTheseDataToNextNodes (**kwargs):
    if kwargs is not None:
        print("#BEGINNING OF DATA RETRIEVING")
        for key, value in kwargs.items():
            print("key ", key)
            print(value)