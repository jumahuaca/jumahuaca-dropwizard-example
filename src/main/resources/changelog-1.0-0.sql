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

CREATE TABLE uva_loan(
  id  SERIAL PRIMARY KEY,
  loan_date date NOT NULL,
  loan_dni_holder int NOT NULL,
  loan_dni_coholder int NOT NULL,
  pesos_value numeric(10,2) NOT NULL,
  uva_value numeric(10,2) NOT NULL
 );
 
 CREATE TABLE uva_loan_fee(
  loan_id  int NOT NULL,
  fee_number int NOT NULL,
  fee_date date NOT NULL,
  initial_capital numeric(10,2) NOT NULL,
  initial_interest numeric(10,2) NOT NULL,
  initial_total numeric(10,2) NOT NULL,
  final_capital numeric(10,2)  NULL,
  final_interest numeric(10,2) NULL,
  final_total numeric(10,2) NULL,
  PRIMARY KEY (loan_id,fee_number),
  FOREIGN KEY (loan_id) REFERENCES uva_loan (id)
  );
 
 