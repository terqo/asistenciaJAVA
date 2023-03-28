var consumer_key        = 'h6zHqiAEUXstLwc8hA8IvNOGm '
var consumer_secret     = 'tLbrYCZ1upuThCucBhYytSDBoipHTvtKuJDJmxYyS5heFSkW1e'
var access_token        = '2422708117-zCPTBDZZ8rfe98sziP3Z8xcCUAdTczlhh9TmpEG'
var access_token_secret = 'GvTKHMEe4dXn6U3FKGcH8BXcoyO89M43Uhq2aiAnTBRJv'

import tweepy
auth = tweepy.OAuthHandler("CONSUMER KEY HERE", "CONSUMER KEY SECRET HERE")
auth.set_access_token("ACCESS TOKEN HERE", "ACCESS TOKEN SECRET HERE")
api = tweepy.API(auth)
print ("Tweet From Terminal, Made By @iCrazeiOS On Twitter!")
print ("Twitter For........")
tweet = input("What Would You Like To Tweet? ")
api.update_status(status =(tweet))
print ("Done!")