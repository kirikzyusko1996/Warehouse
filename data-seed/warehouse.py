import random
from faker import Faker
import reverse_geocode as rg
from mpl_toolkits.basemap import Basemap

bm = Basemap()   # default: projection='cyl'
fake = Faker()
fake.random.seed(5467)
# lat - y
# lng - x

print("%04d" % (10,))

for k in range(4, 204, 1):
    version = "%04d" % (k,)
    first_record = True
    with open(f"../data-analysis-module/flyway/sql/V{version}__added_warehouses_p{k}.sql", "w") as text_file:
        text_file.write('INSERT INTO `warehouse` (name, status, x, y, cca2, id_warehouse_company, created_at) VALUES \n')
        for i in range(10000):
            name = fake.company()
            lat = fake.latitude()
            lon = fake.longitude()
            coordinates = (lat, lon),
            result = rg.search(coordinates) # default mode = 2
            warehouse_company_id = random.randrange(1, 46381, 1)
            if (k * 10000 + i) % 50000 == 0:
                print(k * 10000 + i)
            created_at = fake.date_time_this_decade(before_now=True, after_now=False, tzinfo=None)
            if bm.is_land(lon, lat):
                country = result[0].get('country_code')
                if not first_record:
                    text_file.write(',\n')
                text_file.write(f'(\'{name}\',1,{lat},{lon},\'{country}\',{warehouse_company_id},\'{created_at}\')')

                if first_record:
                    first_record = False
        text_file.write(';')
