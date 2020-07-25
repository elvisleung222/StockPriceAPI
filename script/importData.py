import requests
import yfinance as yf

stock_price_api_base = 'http://localhost:8080'
historical_price_endpoint = stock_price_api_base + '/historical-prices'
stock_endpoint = stock_price_api_base + '/stocks'

symbols = ['MSFT', 'TSLA', 'NIO', 'AAPL', 'BABA']


def to_StockPriceDTO(row):
    return {
        'date': row[0].strftime("%Y-%m-%d"),
        'open': row[1]['Open'],
        'high': row[1]['High'],
        'low': row[1]['Low'],
        'close': row[1]['Close'],
        'volume': int(row[1]['Volume']),
    }


for symbol in symbols:
    payload = {
        'symbol': symbol,
        'historicalPrices': []
    }
    ticker = yf.Ticker(symbol)
    history = ticker.history(period='5d')
    for row in history.iterrows():
        payload['historicalPrices'].append(to_StockPriceDTO(row))
    response = requests.post(historical_price_endpoint, json=[payload])
    print(symbol + ': ' + response.__str__())
