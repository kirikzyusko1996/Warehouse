from faker import Faker
import reverse_geocode as rg
from mpl_toolkits.basemap import Basemap

bm = Basemap()   # default: projection='cyl'
fake = Faker()
fake.random.seed(1234)

with open("company.txt", "w") as text_file:
    for i in range(200000):
        name = fake.company()
        lat = fake.latitude()
        lon = fake.longitude()
        coordinates = (lat, lon),
        result = rg.search(coordinates) # default mode = 2
        if bm.is_land(lon, lat):
            country = result[0].get('country_code')
            text_file.write(f'(\'{name}\',1,{lat},{lon}),\n')
