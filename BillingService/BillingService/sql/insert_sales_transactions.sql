DROP PROCEDURE IF EXISTS insert_sales_transactions;

DELIMITER //
CREATE PROCEDURE insert_sales_transactions(IN EMAIL varchar(50), IN MOVIEID varchar(10), IN QUANTITY int, IN SALEDATE date, IN TOKEN varchar(50))
BEGIN
    INSERT INTO sales(email, movieId, quantity, saleDate) VALUES(EMAIL, MOVIEID, QUANTITY, SALEDATE);
    INSERT INTO transactions(sId, token) VALUES (LAST_INSERT_ID(), TOKEN);
END //
DELIMITER ;
