CREATE TABLE IF NOT EXISTS uva_exchange(
	exchange_day DATE PRIMARY KEY,
	rate numeric(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS uva_loan (
	id int NOT NULL,
	loan_date date NOT NULL,
	loan_dni_holder int NOT NULL,
	loan_dni_coholder int NOT NULL,
	pesos_value numeric(10,2) NOT NULL,
	uva_value numeric(10,2) NOT NULL,
	CONSTRAINT uva_loan_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS uva_loan_fee (
	loan_id int NOT NULL,
	fee_number int NOT NULL,
	fee_date date NOT NULL,
	initial_capital numeric(10,2) NOT NULL,
	initial_interest numeric(10,2) NOT NULL,
	initial_total numeric(10,2) NOT NULL,
	final_capital numeric(10,2) NULL,
	final_interest numeric(10,2) NULL,
	final_total numeric(10,2) NULL,
	CONSTRAINT uva_loan_fee_pkey PRIMARY KEY (loan_id, fee_number),
	CONSTRAINT uva_loan_fee_loan_id_fkey FOREIGN KEY (loan_id) REFERENCES uva_loan(id)
);
