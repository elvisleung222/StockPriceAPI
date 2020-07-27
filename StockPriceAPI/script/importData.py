import requests
import yfinance as yf
from datetime import datetime

stock_price_api_base = 'http://localhost:8001'
historical_price_endpoint = stock_price_api_base + '/prices'
stock_endpoint = stock_price_api_base + '/stocks'

symbols = ['MSFT', 'TSLA', 'NIO', 'AAPL', 'BABA',
           'GOOG', 'AMZN', 'MRNA', 'NFLX', 'INTC',
           'F', 'GE', 'BAC', 'TSM', 'AUY',
           'CCL', 'WFC', 'BA', 'PCG', 'FE']


def to_StockPriceDTO(row):
    return {
        'date': row[0].strftime("%Y-%m-%d"),
        'open': row[1]['Open'],
        'high': row[1]['High'],
        'low': row[1]['Low'],
        'close': row[1]['Close'],
        'volume': int(row[1]['Volume']),
    }


started = datetime.now()
payload = []
for symbol in symbols:
    data = {
        'symbol': symbol,
        'historicalPrices': []
    }
    ticker = yf.Ticker(symbol)
    # 1d, 5d, 1mo, 3mo, 6mo, 1y, 2y, 5y, 10y, ytd, max
    history = ticker.history(period='5d')
    for row in history.iterrows():
        data['historicalPrices'].append(to_StockPriceDTO(row))
    payload.append(data)

response = requests.post(historical_price_endpoint, json=payload)
print('Added - ' + response.__str__())
# response = requests.delete(stock_endpoint + "?symbols=" + ','.join(symbols))
# print('Deleted - ' + ','.join(symbols) + ': ' + response.__str__())

print('Total time taken : ' + str(datetime.now() - started))
