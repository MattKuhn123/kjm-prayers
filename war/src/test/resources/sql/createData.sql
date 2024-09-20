DELETE FROM `kjm`.`jails`;
DELETE FROM `kjm`.`inmates`;
DELETE FROM `kjm`.`prayers`;
DELETE FROM `kjm`.`users`;

INSERT INTO `kjm`.`jails` (`county`) VALUES ('Kenton'), ('Boone'), ('Grant'), ('Campbell');

INSERT INTO `kjm`.`inmates` (`first_name`, `last_name`, `county`, `birth_day`, `is_male`, `info`) VALUES
('Ralf','Grigoriev','Kenton',STR_TO_DATE('08/04/1990', '%m/%d/%Y'),TRUE,'et commodo vulputate justo in blandit ultrices enim lorem ipsum dolor sit amet consectetuer adipiscing elit proin'),
('Bettina','Venditti','Grant',STR_TO_DATE('09/29/1980', '%m/%d/%Y'),TRUE,'sit  ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae nulla dapibus dolor vel est donec odio justo sollicitudin  tristique fusce congue diam id ornare'),
('Germana','Coan','Boone',STR_TO_DATE('11/18/1965', '%m/%d/%Y'),TRUE,'tincidunt  id ligula suspendisse ornare consequat lectus in est risus auctor'),
('Guthrie','Scain','Campbell',STR_TO_DATE('11/04/2000', '%m/%d/%Y'),TRUE,'ut nunc vestibulum ante  orci luctus et ultrices posuere cubilia curae mauris viverra diam vitae quam suspendisse potenti nullam porttitor lacus at turpis donec posuere'),
('Ransell','Treadgold','Kenton',STR_TO_DATE('12/25/2002', '%m/%d/%Y'),TRUE,'sociis natoque penatibus et  montes nascetur ridiculus mus etiam vel augue vestibulum rutrum rutrum neque aenean auctor gravida sem praesent id massa id nisl venenatis lacinia aenean sit amet justo morbi'),
('Raquela','Tatterton','Kenton',STR_TO_DATE('07/01/1995', '%m/%d/%Y'),TRUE,'orci nullam molestie nibh in lectus pellentesque at nulla suspendisse potenti cras in purus eu magna vulputate luctus'),
('Larry','Bickerton','Grant',STR_TO_DATE('05/02/1950', '%m/%d/%Y'),FALSE,'feugiat et eros vestibulum ac est lacinia nisi venenatis tristique fusce congue diam id ornare imperdiet sapien urna pretium nisl ut volutpat sapien arcu sed augue aliquam  platea'),
('Milly','Watson','Boone',STR_TO_DATE('08/14/1976', '%m/%d/%Y'),TRUE,'orci mauris lacinia sapien quis libero nullam sit amet turpis elementum ligula vehicula consequat morbi a ipsum integer a nibh in quis justo maecenas rhoncus aliquam lacus morbi quis  viverra pede ac'),
('Alfreda','Yakubovich','Campbell',STR_TO_DATE('07/31/1969', '%m/%d/%Y'),TRUE,'felis sed lacus morbi sem mauris laoreet ut rhoncus aliquet pulvinar sed nisl nunc rhoncus dui vel sem sed sagittis nam congue risus semper porta pellentesque at'),
('Gunilla','Stut','Kenton',STR_TO_DATE('10/02/1932', '%m/%d/%Y'),FALSE,'nullam varius nulla facilisi cras non velit nec nisi vulputate nonummy maecenas tincidunt lacus at velit vivamus');

INSERT INTO `kjm`.`prayers` (`first_name`, `last_name`, `county`, `date`, `prayer`) VALUES 
('Ralf', 'Grigoriev', 'Kenton', STR_TO_DATE('12/25/2023', '%m/%d/%Y'), 'I need prayers for everything!'),
('Ralf', 'Grigoriev', 'Kenton', STR_TO_DATE('12/26/2023', '%m/%d/%Y'), 'I still need prayers for everything!'),
('Germana', 'Coan', 'Boone', STR_TO_DATE('10/05/2023', '%m/%d/%Y'), 'orci mauris lacinia sapien quis libero nullam sit'),
('Guthrie', 'Scain', 'Campbell', STR_TO_DATE('09/20/2020', '%m/%d/%Y'), 'tincidunt  id ligula suspendisse ornare consequat lectus in est risus auctor');

INSERT INTO `kjm`.`users` (`email`) VALUES ('mlkuhn@tva.gov');