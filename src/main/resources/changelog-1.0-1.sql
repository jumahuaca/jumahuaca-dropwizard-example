INSERT INTO public.uva_loan
(loan_date, loan_dni_holder, loan_dni_coholder, pesos_value, uva_value)
VALUES('2017-09-20', 11111111, 22222222, 1132800.0, 56470.59);

INSERT INTO public.uva_loan_fee
(loan_id, fee_number, fee_date, initial_capital, initial_interest, initial_total, final_capital, final_interest, final_total)
VALUES(1, 1, '2017-11-10', 3265.78, 5539.86, 8805.64, 3356.948335, 5694.512124, 9051.460459);

INSERT INTO public.uva_loan_fee
(loan_id, fee_number, fee_date, initial_capital, initial_interest, initial_total, final_capital, final_interest, final_total)
VALUES(1, 2, '2017-12-11', 3275.31, 3294.47, 6569.78, NULL, NULL, NULL);

INSERT INTO public.uva_loan_fee
(loan_id, fee_number, fee_date, initial_capital, initial_interest, initial_total, final_capital, final_interest, final_total)
VALUES(1, 3, '2018-01-10', 3284.86, 3284.92, 6569.78, NULL, NULL, NULL);

INSERT INTO public.uva_exchange
(exchange_day, rate)
VALUES('2017-09-20', 20.06);
INSERT INTO public.uva_exchange
(exchange_day, rate)
VALUES('2017-11-10', 20.62);
INSERT INTO public.uva_exchange
(exchange_day, rate)
VALUES('2017-12-11', 20.93);
INSERT INTO public.uva_exchange
(exchange_day, rate)
VALUES('2018-01-10', 21.26);

