import ind
import bottle

app = ind.App()

if __name__ == '__main__':
    bottle.run(host = '0.0.0.0', port = 8000)
