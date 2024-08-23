CREATE TABLE catalogue_item (
    id          INT AUTO_INCREMENT NOT NULL,
    caption     VARCHAR(128) NOT NULL,
    description TEXT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE cart (
    id INT AUTO_INCREMENT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE cart_item (
    cart_id     INT NOT NULL,
    item_id     INT NOT NULL,
    quantity    INT NOT NULL,
    PRIMARY KEY (cart_id, item_id),
    FOREIGN KEY (cart_id) REFERENCES cart(id),
    FOREIGN KEY (item_id) REFERENCES catalogue_item(id)
);

INSERT INTO catalogue_item (caption, description) VALUES
  ('Leather Sofa',  'A very nice and comfortable sofa'),
  ('Wooden Table',  'A large table ideal for 6 to 8 people'),
  ('Plastic Chair', 'A robust plastic chair ideal for children and adults alike'),
  ('Mug',           'The ideal way to start the day'),
  ('LED TV',        'A very large TV set, ideal for those who like to bring TV shows and spend time watching TV');

INSERT INTO cart () VALUES
  (),
  (),
  ();

INSERT INTO cart_item (cart_id, item_id, quantity) VALUES
  (1, 1, 1),
  (1, 5, 1),
  (2, 2, 1),
  (2, 3, 6),
  (3, 4, 4);
