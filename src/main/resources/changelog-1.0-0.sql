CREATE TABLE uva_exchange(
	exchange_day DATE PRIMARY KEY,
	rate numeric(10,2) NOT NULL
);

CREATE TABLE uva_scraping_process(
  id  SERIAL PRIMARY KEY,
  from_date date NOT NULL,
  to_date date NOT NULL,
  status varchar(50) NOT NULL,
  process_date TIMESTAMPTZ NOT NULL
);

