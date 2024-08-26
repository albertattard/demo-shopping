CREATE TABLE catalogue_item (
    id          INT AUTO_INCREMENT NOT NULL,
    caption     VARCHAR(128) NOT NULL,
    description TEXT NOT NULL,
    processed   BOOLEAN DEFAULT false,
    PRIMARY KEY (id)
);

INSERT INTO catalogue_item (caption, description, processed) VALUES
  ('Leather Sofa',  'A very nice and comfortable sofa', true),
  ('Wooden Table',  'A large table ideal for 6 to 8 people', true),
  ('Plastic Chair', 'A robust plastic chair ideal for children and adults alike', true),
  ('Mug',           'The ideal way to start the day', true),
  ('LED TV',        'A very large TV set, ideal for those who like to bring TV shows and spend time watching TV', true);
