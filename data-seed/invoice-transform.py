import pandas as pd
import datetime

def get_weekday(i): # 0-6
    date = datetime.datetime.strptime(i, "%Y-%m-%d")
    return date.isocalendar()[2]

def is_weekend(i):
    return 1 if get_weekday(i) > 5 else 0

holiday_list = [
    {
        'month': 1,
        'day': 1
    },
    {
        'month': 1,
        'day': 7
    },
    {
        'month': 3,
        'day': 8
    },
    {
        'month': 5,
        'day': 1
    },
    {
        'month': 5,
        'day': 7
    },
    {
        'month': 5,
        'day': 9
    },
    {
        'month': 7,
        'day': 3
    },
    {
        'month': 11,
        'day': 7
    },
    {
        'month': 12,
        'day': 25
    }
]

def is_in_holiday_list(i):
    year,month,day=i.split('-')

    for i in holiday_list:
        if i['month'] == int(month) and i['day'] == int(day):
            return 1

    return 0

def is_holiday(i):
    return is_weekend(i) or is_in_holiday_list(i)

def week_of_year(i): # 0-52
    date = datetime.datetime.strptime(i, "%Y-%m-%d")
    return date.isocalendar()[1]

def get_trend(data):
    struct = {}
    for i in range(len(data)):
        for j in range(14):
            key = f'day_{j+1}'
            if struct.get(key) is None:
                struct[key] = []
            index = i - 14 + j
            if index < 0:
                struct[key].append(0)
            else:
                struct[key].append(data[index])

    return struct

def merge(df1, df2):
    for i in range(14):
        key = f'day_{i+1}'
        df1[key] = df2[key]

    return df1


################################################### MAIN ############################

df = pd.read_csv("series.csv")

###################### ENCODE DATE ###################

date = list(df['date'])

week = []
for i in date:
    week.append(week_of_year(i))
df['week'] = week

weekday = []
for i in date:
    weekday.append(get_weekday(i))
df['weekday'] = weekday

holiday = []
for i in date:
    holiday.append(is_holiday(i))
df['holiday'] = holiday

########################## TREND CONVERSION ######################################

input = list(df['input'])
#output = list(df['output'])

df = merge(df, get_trend(input))


########################## ENCODE DATE BY ONE HOT SCHEME #########################

# use pd.concat to join the new columns with your original dataframe
df = pd.concat([df, pd.get_dummies(df['weekday'], prefix='weekday')], axis=1)
# now drop the original 'country' column (you don't need it anymore)
df.drop(['weekday'], axis=1, inplace=True)

# use pd.concat to join the new columns with your original dataframe
df = pd.concat([df, pd.get_dummies(df['week'], prefix='week')], axis=1)
# now drop the original 'country' column (you don't need it anymore)
df.drop(['week'], axis=1, inplace=True)
df.drop(['date'], axis=1, inplace=True)

##################################################################################

df.to_csv('series-transformed.csv', encoding='utf-8', index=False)
