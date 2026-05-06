import pymysql

conn = pymysql.connect(
    host='localhost',
    user='root',
    password='123456',
    database='charity_db',
    charset='utf8mb4'
)
cursor = conn.cursor()

cursor.execute("UPDATE organizations SET name = CONVERT(BINARY CONVERT(name USING latin1) USING utf8mb4), contact = CONVERT(BINARY CONVERT(contact USING latin1) USING utf8mb4) WHERE deleted = 0")

conn.commit()

cursor.execute('SELECT id, name, contact, phone FROM organizations WHERE deleted = 0')
for row in cursor.fetchall():
    print(row)

conn.close()
print('修复完成')