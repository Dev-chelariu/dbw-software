INSERT INTO dbw_product (availability, category, cod_product, description, name, price, stock_count, unit_measure)
VALUES
    (1, 'Electronics', 07601, 'High-end smartphone with advanced features', 'Samsung Galaxy S21 Ultra', 1299.99, 100, 'PIECE'),
    (1, 'Home Appliances', 002, 'Powerful blender with multiple speed settings', 'KitchenAid Blender', 199.99, 50, 'PIECE'),
    (1, 'Clothing', 003, 'Classic cotton t-shirt with embroidered logo', 'Tommy Hilfiger Logo T-Shirt', 29.99, 200, 'PIECE'),
    (1, 'Beauty', 004, 'Gentle cleansing foam for sensitive skin', 'Cetaphil Gentle Skin Cleanser', 14.99, 500, 'PIECE'),
    (1, 'Sports', 005, 'Durable and lightweight tennis racket for intermediate players', 'Wilson Burn 100S Tennis Racket', 199.99, 20, 'PIECE'),
    (1, 'Food and Beverages', 006, 'Organic dark chocolate with sea salt and almonds', 'Alter Eco Dark Salted Almonds Chocolate', 4.99, 1000, 'PIECE'),
    (1, 'Office Supplies', 007, 'Stylish fountain pen with medium nib', 'Parker Urban Fountain Pen', 69.99, 30, 'PIECE'),
    (1, 'Home and Garden', 008, 'Comfortable cotton bed sheets with embroidered design', 'Brooklinen Luxe Core Sheet Set', 179.99, 100, 'SET'),
    (1, 'Toys and Games', 009, 'Interactive robotic puppy that responds to touch and voice commands', 'FurReal Friends Ricky, the Trick-Lovin\', 129.99, 10, 'PIECE'),
    (1, 'Books', 010, 'Bestselling novel about a woman journey of self-discovery', 'Eat, Pray, Love by Elizabeth Gilbert', 12.99, 300, 'PIECE');


CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE dbw_product
(
    cod_product  BIGINT       NOT NULL,
    name         VARCHAR(255) NOT NULL,
    price        INTEGER      NOT NULL,
    category     VARCHAR(255) NOT NULL,
    description  VARCHAR(255),
    unit_measure VARCHAR(255),
    stock_count  INTEGER      NOT NULL,
    availability VARCHAR(255),
    CONSTRAINT pk_dbw_product PRIMARY KEY (cod_product)
);