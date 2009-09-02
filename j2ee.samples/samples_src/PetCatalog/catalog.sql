DROP TABLE item;
DROP TABLE product;
DROP TABLE category;
DROP TABLE address;

create table category(
    id INT NOT NULL,
    name VARCHAR(25) NOT NULL,
    description VARCHAR(255) NOT NULL,
    imageurl VARCHAR(55),
    primary key (id)
);

CREATE TABLE product (
 id INT NOT NULL,
 category_id INT NOT NULL,
 name VARCHAR(25) NOT NULL,
 description VARCHAR(255) NOT NULL,
 imageurl VARCHAR(55),
 primary key (id),
 foreign key (category_id) references category(id)
);

CREATE TABLE address (
 id INT NOT NULL,
 street1 VARCHAR(55) NOT NULL,
 street2 VARCHAR(55),
 city VARCHAR(55) NOT NULL,
 state VARCHAR(25) NOT NULL,
 zip VARCHAR(5) NOT NULL,
 latitude DECIMAL(14,10) NOT NULL,
 longitude DECIMAL(14,10) NOT NULL,
 primary key (id)
);

CREATE TABLE item (
 id INT NOT NULL,
 product_id INT NOT NULL,
 name VARCHAR(30) NOT NULL,
 description VARCHAR(500) NOT NULL,
 imageurl VARCHAR(55),
 imagethumburl VARCHAR(55),
 price DECIMAL(14,2) NOT NULL,
 address_id INT NOT NULL,
 primary key (id),
 foreign key (address_id) references address(id),
 foreign key (product_id) references product(id)
);

INSERT INTO category VALUES(1, 'Cats', 'Loving and finicky friends', 'cats_icon.gif');
INSERT INTO category VALUES(2, 'Dogs', 'Loving and furry friends', 'dogs_icon.gif');
INSERT INTO category VALUES(3, 'Birds', 'Loving and feathery friends', 'birds_icon.gif');
INSERT INTO category VALUES(4, 'Reptiles', 'Loving and scaly friends', 'reptiles_icon.gif');
INSERT INTO category VALUES(5, 'Fish', 'Loving aquatic friends', 'fish_icon.gif');

INSERT INTO product VALUES(1, 1, 'Hairy Cat', 'Great for reducing mouse populations', 'cat1.gif');
INSERT INTO product VALUES(2, 1, 'Groomed Cat', 'Friendly house cat keeps you away from the vacuum', 'cat2.gif');
INSERT INTO product VALUES(3, 2, 'Medium Dogs', 'Friendly dog from England', 'dog1.gif');
INSERT INTO product VALUES(4, 2, 'Small Dogs', 'Great companion dog to sit on your lap','dog2.gif');
INSERT INTO product VALUES(5, 3, 'Parrot', 'Friend for a lifetime.', 'bird1.gif');
INSERT INTO product VALUES(6, 3, 'Exotic', 'Impress your friends with your colorful friend.','bird2.gif');
INSERT INTO product VALUES(7, 5, 'Small Fish', 'Fits nicely in a small aquarium.','fish2.gif');
INSERT INTO product VALUES(8, 5, 'Large Fish', 'Need a large aquarium.','fish3.gif');
INSERT INTO product VALUES(9, 4, 'Slithering Reptiles', 'Slides across the floor.','lizard1.gif');
INSERT INTO product VALUES(10, 4, 'Crawling Reptiles', 'Uses legs to move fast.','lizard2.gif');

INSERT INTO address VALUES(1, 'W el Camino Real & Castro St', '', 'Mountain View','CA','94040',37.38574,-122.083973);
INSERT INTO address VALUES(2, 'Shell Blvd & Beach Park Blvd', '', 'Foster City','CA','94404',37.546935,-122.263978);
INSERT INTO address VALUES(3, 'River Oaks Pky & Village Center Dr', '', 'San Jose','CA','95134',37.398259,-121.922367);
INSERT INTO address VALUES(4, 'S 1st St & W Santa Clara St', '', 'San Jose','CA','95113',37.336141,-121.890666);
INSERT INTO address VALUES(5, '1st St & Market St ', '', 'San Francisco','CA','94105',37.791028,-122.399082);
INSERT INTO address VALUES(6, 'Paseo Padre Pky & Fremont Blvd', '', 'Fremont','CA','94555',37.575035,-122.041273);
INSERT INTO address VALUES(7, 'W el Camino Real & S Mary Ave', '', 'Sunnyvale','CA','94087',37.371641,-122.048772);
INSERT INTO address VALUES(8, 'Bay Street and Columbus Ave ', '', 'San Francisco','CA','94133',37.805328,-122.416882);
INSERT INTO address VALUES(9, 'El Camino Real & Scott Blvd', '', 'Santa Clara','CA','95050',37.352141 ,-121.959569);
INSERT INTO address VALUES(10, 'W el Camino Real & Hollenbeck Ave', '', 'Sunnyvale','CA','94087',37.369941,-122.041271);
INSERT INTO address VALUES(11, 'S Main St & Serra Way', '', 'Milpitas','CA','95035',37.428112,-121.906505);
INSERT INTO address VALUES(12, 'Great Mall Pky & S Main St', '', 'Milpitas','CA','95035',37.414722,-121.902085);
INSERT INTO address VALUES(13, 'Valencia St &  16th St', '', 'San Francisco','CA','94103',37.764985,-122.421886);
INSERT INTO address VALUES(14, 'S 1st St & W Santa Clara St', '', 'San Jose','CA','95113',37.336141,-121.890666);
INSERT INTO address VALUES(15, 'Bay Street and Columbus Ave ', '', 'San Francisco','CA','94133',37.805328,-122.416882);
INSERT INTO address VALUES(16, 'El Camino Real & Scott Blvd', '', 'Santa Clara','CA','95050',37.352141 ,-121.959569);
INSERT INTO address VALUES(17, 'Millbrae Ave &  Willow Ave', '', 'Millbrae ','CA','94030',37.596635,-122.391083);
INSERT INTO address VALUES(18, 'Leavesley Rd & Monterey Rd', '', 'Gilroy','CA','95020',37.019447,-121.574953);
INSERT INTO address VALUES(19, 'S Main St & Serra Way', '', 'Milpitas','CA','95035',37.428112,-121.906505);
INSERT INTO address VALUES(20, '24th St &  Dolores St', '', 'San Francisco','CA','94114',37.75183,-122.424982);
INSERT INTO address VALUES(21, 'Great Mall Pky & S Main St', '', 'Milpitas','CA','95035',37.414722,-121.902085);
INSERT INTO address VALUES(22, 'Grant Rd & South Dr ', '', 'Mountain view','CA','94040',37.366941,-122.078073);
INSERT INTO address VALUES(23, '25th St &  Dolores St', '', 'San Francisco','CA','94114',37.75013,-122.424782);
INSERT INTO address VALUES(24, 'Ellis St & National Ave', '', 'Mountain view','CA','94043',37.40094,-122.052272);
INSERT INTO address VALUES(25, '1st St & Market St ', '', 'San Francisco','CA','94105',37.791028,-122.399082);
INSERT INTO address VALUES(26, 'San Antonio Rd & Middlefield Rd', '', 'Palo Alto','CA','94303',37.416239,-122.103474);
INSERT INTO address VALUES(27, '20th St &  Dolores St', '', 'San Francisco','CA','94114',37.75823,-122.425582);
INSERT INTO address VALUES(28, 'River Oaks Pky & Village Center Dr', '', 'San Jose','CA','95134',37.398259,-121.922367);
INSERT INTO address VALUES(29, 'Dolores St &  San Jose Ave', '', 'San Francisco','CA','94110',37.74023,-122.423782);
INSERT INTO address VALUES(30, 'Leavesley Rd & Monterey Rd', '', 'Gilroy','CA','95020',37.019447,-121.574953);
INSERT INTO address VALUES(31, 'Palm Dr & Arboretum Rd', '', 'Stanford','CA','94305',37.437838,-122.166975);
INSERT INTO address VALUES(32, 'Millbrae Ave &  Willow Ave', '', 'Millbrae ','CA','94030',37.596635,-122.391083);
INSERT INTO address VALUES(33, 'Cesar Chavez St & Sanchez', '', 'San Francisco','CA','94131',37.74753,-122.428982);
INSERT INTO address VALUES(34, 'University Ave & Middlefield Rd', '', 'Palo Alto','CA','94301',37.450638,-122.156975);
INSERT INTO address VALUES(35, 'Bay Street and Columbus Ave ', '', 'San Francisco','CA','94133',37.805328,-122.416882);
INSERT INTO address VALUES(36, 'Telegraph Ave & Bancroft Way', '', 'Berkeley','CA','94704',37.868825,-122.25978);
INSERT INTO address VALUES(37, '1st St & Market St ', '', 'San Francisco','CA','94105',37.791028,-122.399082);
INSERT INTO address VALUES(38, 'San Antonio Rd & Middlefield Rd', '', 'Palo Alto','CA','94303',37.416239,-122.103474);
INSERT INTO address VALUES(39, '20th St &  Dolores St', '', 'San Francisco','CA','94114',37.75823,-122.425582);
INSERT INTO address VALUES(40, 'River Oaks Pky & Village Center Dr', '', 'San Jose','CA','95134',37.398259,-121.922367);
INSERT INTO address VALUES(41, 'Dolores St &  San Jose Ave', '', 'San Francisco','CA','94110',37.74023,-122.423782);
INSERT INTO address VALUES(42, '4th St & Howard St', '', 'San Francisco','CA','94103',37.783229,-122.402582);
INSERT INTO address VALUES(43, 'Palm Dr & Arboretum Rd', '', 'Stanford','CA','94305',37.437838,-122.166975);
INSERT INTO address VALUES(44, 'Campbell St & Riverside Ave', '', 'Santa Cruz','CA','95060',36.96985,-122.019473);
INSERT INTO address VALUES(45, 'Cesar Chavez St & Sanchez', '', 'San Francisco','CA','94131',37.74753,-122.428982);
INSERT INTO address VALUES(46, 'University Ave & Middlefield Rd', '', 'Palo Alto','CA','94301',37.450638,-122.156975);
INSERT INTO address VALUES(47, 'W el Camino Real & S Mary Ave', '', 'Sunnyvale','CA','94087',37.371641,-122.048772);
INSERT INTO address VALUES(48, 'Telegraph Ave & Bancroft Way', '', 'Berkeley','CA','94704',37.868825,-122.25978);
INSERT INTO address VALUES(49, '1st St & Market St ', '', 'San Francisco','CA','94105',37.791028,-122.399082);
INSERT INTO address VALUES(50, 'San Antonio Rd & Middlefield Rd', '', 'Palo Alto','CA','94303',37.416239,-122.103474);
INSERT INTO address VALUES(51, '20th St &  Dolores St', '', 'San Francisco','CA','94114',37.75823,-122.425582);
INSERT INTO address VALUES(52, 'River Oaks Pky & Village Center Dr', '', 'San Jose','CA','95134',37.398259,-121.922367);
INSERT INTO address VALUES(53, 'Dolores St &  San Jose Ave', '', 'San Francisco','CA','94110',37.74023,-122.423782);
INSERT INTO address VALUES(54, '4th St & Howard St', '', 'San Francisco','CA','94103',37.783229,-122.402582);
INSERT INTO address VALUES(55, 'Palm Dr & Arboretum Rd', '', 'Stanford','CA','94305',37.437838,-122.166975);
INSERT INTO address VALUES(56, 'W el Camino Real & S Mary Ave', '', 'Sunnyvale','CA','94087',37.371641,-122.048772);
INSERT INTO address VALUES(57, 'Campbell St & Riverside Ave', '', 'Santa Cruz','CA','95060',36.96985,-122.019473);
INSERT INTO address VALUES(58, 'University Ave & Middlefield Rd', '', 'Palo Alto','CA','94301',37.450638,-122.156975);
INSERT INTO address VALUES(59, 'Bay Street and Columbus Ave ', '', 'San Francisco','CA','94133',37.805328,-122.416882);
INSERT INTO address VALUES(60, 'Telegraph Ave & Bancroft Way', '', 'Berkeley','CA','94704',37.868825,-122.25978);
INSERT INTO address VALUES(61, 'San Antonio Rd & Middlefield Rd', '', 'Palo Alto','CA','94303',37.416239,-122.103474);
INSERT INTO address VALUES(62, '20th St &  Dolores St', '', 'San Francisco','CA','94114',37.75823,-122.425582);
INSERT INTO address VALUES(63, 'River Oaks Pky & Village Center Dr', '', 'San Jose','CA','95134',37.398259,-121.922367);
INSERT INTO address VALUES(64, 'Dolores St &  San Jose Ave', '', 'San Francisco','CA','94110',37.74023,-122.423782);
INSERT INTO address VALUES(65, 'W el Camino Real & S Mary Ave', '', 'Sunnyvale','CA','94087',37.371641,-122.048772);
INSERT INTO address VALUES(66, 'Palm Dr & Arboretum Rd', '', 'Stanford','CA','94305',37.437838,-122.166975);
INSERT INTO address VALUES(67, 'Millbrae Ave &  Willow Ave', '', 'Millbrae ','CA','94030',37.596635,-122.391083);
INSERT INTO address VALUES(68, 'Cesar Chavez St & Sanchez', '', 'San Francisco','CA','94131',37.74753,-122.428982);
INSERT INTO address VALUES(69, 'University Ave & Middlefield Rd', '', 'Palo Alto','CA','94301',37.450638,-122.156975);
INSERT INTO address VALUES(70, 'Bay Street and Columbus Ave ', '', 'San Francisco','CA','94133',37.805328,-122.416882);
INSERT INTO address VALUES(71, 'Leavesley Rd & Monterey Rd', '', 'Gilroy','CA','95020',37.019447,-121.574953);
INSERT INTO address VALUES(72, 'Campbell St & Riverside Ave', '', 'Santa Cruz','CA','95060',36.96985,-122.019473);
INSERT INTO address VALUES(73, '20th St &  Dolores St', '', 'San Francisco','CA','94114',37.75823,-122.425582);
INSERT INTO address VALUES(74, 'River Oaks Pky & Village Center Dr', '', 'San Jose','CA','95134',37.398259,-121.922367);
INSERT INTO address VALUES(75, 'Dolores St &  San Jose Ave', '', 'San Francisco','CA','94110',37.74023,-122.423782);
INSERT INTO address VALUES(76, '4th St & Howard St', '', 'San Francisco','CA','94103',37.783229,-122.402582);
INSERT INTO address VALUES(77, 'Palm Dr & Arboretum Rd', '', 'Stanford','CA','94305',37.437838,-122.166975);
INSERT INTO address VALUES(78, 'Millbrae Ave &  Willow Ave', '', 'Millbrae ','CA','94030',37.596635,-122.391083);
INSERT INTO address VALUES(79, 'Campbell St & Riverside Ave', '', 'Santa Cruz','CA','95060',36.96985,-122.019473);
INSERT INTO address VALUES(80, 'University Ave & Middlefield Rd', '', 'Palo Alto','CA','94301',37.450638,-122.156975);
INSERT INTO address VALUES(81, 'Grant Rd & South Dr ', '', 'Mountain view','CA','94040',37.366941,-122.078073);
INSERT INTO address VALUES(82, 'Telegraph Ave & Bancroft Way', '', 'Berkeley','CA','94704',37.868825,-122.25978);
INSERT INTO address VALUES(83, 'San Antonio Rd & Middlefield Rd', '', 'Palo Alto','CA','94303',37.416239,-122.103474);
INSERT INTO address VALUES(84, '20th St &  Dolores St', '', 'San Francisco','CA','94114',37.75823,-122.425582);
INSERT INTO address VALUES(85, 'River Oaks Pky & Village Center Dr', '', 'San Jose','CA','95134',37.398259,-121.922367);
INSERT INTO address VALUES(86, 'Dolores St &  San Jose Ave', '', 'San Francisco','CA','94110',37.74023,-122.423782);
INSERT INTO address VALUES(87, '4th St & Howard St', '', 'San Francisco','CA','94103',37.783229,-122.402582);
INSERT INTO address VALUES(88, 'Palm Dr & Arboretum Rd', '', 'Stanford','CA','94305',37.437838,-122.166975);
INSERT INTO address VALUES(89, 'Millbrae Ave &  Willow Ave', '', 'Millbrae ','CA','94030',37.596635,-122.391083);
INSERT INTO address VALUES(90, 'Paseo Padre Pky & Fremont Blvd', '', 'Fremont','CA','94555',37.575035,-122.041273);
INSERT INTO address VALUES(91, 'University Ave & Middlefield Rd', '', 'Palo Alto','CA','94301',37.450638,-122.156975);
INSERT INTO address VALUES(92, 'El Camino Real & Scott Blvd', '', 'Santa Clara','CA','95050',37.352141 ,-121.959569);
INSERT INTO address VALUES(93, 'Telegraph Ave & Bancroft Way', '', 'Berkeley','CA','94704',37.868825,-122.25978);
INSERT INTO address VALUES(94, 'San Antonio Rd & Middlefield Rd', '', 'Palo Alto','CA','94303',37.416239,-122.103474);
INSERT INTO address VALUES(95, '20th St &  Dolores St', '', 'San Francisco','CA','94114',37.75823,-122.425582);
INSERT INTO address VALUES(96, 'River Oaks Pky & Village Center Dr', '', 'San Jose','CA','95134',37.398259,-121.922367);
INSERT INTO address VALUES(97, 'Dolores St &  San Jose Ave', '', 'San Francisco','CA','94110',37.74023,-122.423782);
INSERT INTO address VALUES(98, 'Campbell St & Riverside Ave', '', 'Santa Cruz','CA','95060',36.96985,-122.019473);
INSERT INTO address VALUES(99, 'Palm Dr & Arboretum Rd', '', 'Stanford','CA','94305',37.437838,-122.166975);
INSERT INTO address VALUES(100, 'Leavesley Rd & Monterey Rd', '', 'Gilroy','CA','95020',37.019447,-121.574953);
INSERT INTO address VALUES(101, 'Cesar Chavez St & Sanchez', '', 'San Francisco','CA','94131',37.74753,-122.428982);
INSERT INTO address VALUES(102, 'University Ave & Middlefield Rd', '', 'Palo Alto','CA','94301',37.450638,-122.156975);

INSERT INTO item VALUES(1, 1, 'Friendly Cat', 'This black and white colored cat is super friendly. Anyone passing by your front yard will find him puring at their feet and trying to make a new friend. His name is Anthony, but I call him Ant as a nickname since he loves to eat ants and other insects.', 'anthony.jpg','anthony-s.jpg', '307.10',1);
INSERT INTO item VALUES(2, 1, 'Fluffy Cat', 'A great pet for a hair stylist! Have fun combing Bailey''s silver mane. Maybe trim his whiskers? He is very patient and loves to be pampered.', 'bailey.jpg','bailey-s.jpg', '307',2);
INSERT INTO item VALUES(3, 2, 'Sneaky Cat', 'My cat is so sneaky. He is so curious that he just has to poke his nose into everything going on in the house. Everytime I turn around, BAM, he is in the room peaking at what I am doing. Nothing escapes his keen eye. He should be a spy in the CIA!', 'bob.jpg','bob-s.jpg', '307.20',3);
INSERT INTO item VALUES(4, 2, 'Lazy Cat', 'A great pet to lounge on the sofa with. If you want a friend to watch TV with, this is the cat for you. Plus, she wont even ask for the remote! Really, could you ask for a better friend to lounge with?', 'chantelle.jpg','chantelle-s.jpg', '307.30',4);
INSERT INTO item VALUES(5, 1, 'Old Cat', 'A great old pet retired from duty in the circus. This fully-trained tiger is looking for a place to retire. Loves to roam free and loves to eat other animals.', 'charlie.jpg','charlie-s.jpg', '307',5);
INSERT INTO item VALUES(6, 2, 'Young Female cat', 'A great young pet to chase around. Loves to play with a ball of string. Bring some instant energy into your home.', 'elkie.jpg','elkie-s.jpg', '307.40',6);
INSERT INTO item VALUES(7, 1, 'Playful Female Cat', 'A needy pet. This cat refuses to grow up. Do you like playful spirits? I need lots of attention. Please do not leave me alone, not even for a minute.', 'faith.jpg','faith-s.jpg', '307',7);
INSERT INTO item VALUES(8, 1, 'White Fluffy Cat', 'This fluffy white cat looks like a snowball. Plus, she likes playing outside in the snow and it looks really cool to see this snowball cat run around on the ski slopes. I hope you have white carpet as this cat sheds lots of hair.', 'gaetano.jpg','gaetano-s.jpg', '307.50',8);
INSERT INTO item VALUES(9, 2, 'Tiger Stripe Cat', 'This little tiger thinks it has big teeth. A great wild pet for an adventurous person. May eat your other pets so be careful- just kidding. This little tiger is affectionate.', 'harmony.jpg','harmony-s.jpg', '307',9);
INSERT INTO item VALUES(10, 2, 'Alley Cat', 'Meow Meow in the back alley cat fights! This cat keeps the racoons away, but still has class.', 'katzen.jpg','katzen-s.jpg', '307.60',10);
INSERT INTO item VALUES(11, 2, 'Speedy Cat', 'Fastest and coolest cat in town. If you always wanted to own a cheetah, this cat is even faster and better looking. No dog could ever catch this bolt of lightening.', 'mario.jpg','mario-s.jpg', '307',11);
INSERT INTO item VALUES(12, 1, 'Stylish Cat', 'A high maintenance cat for an owner with time. This cat needs pampering: comb it hair, brush its teeth, wash its fur, paint its claws. For all you debutantes, let the world know you have arrived in style with this snooty cat in your purse!', 'mimi.jpg','mimi-s.jpg', '307.70',12);
INSERT INTO item VALUES(13, 1, 'Smelly Cat', 'A great pet with its own song to sing with your fiends. "Smelly cat, Smelly cat ..." Need an excuse for that funky odor in your house? Smelly cat is the answer.', 'monique.jpg','monique-s.jpg', '307.80',13);
INSERT INTO item VALUES(14, 1, 'Saber Cat', 'A great watch pet. Want to keep your roommates from stealing the beer from your refrigerator? This big-toothed crazy cat is better than a watchdog. Just place him on top of the refrigerator and watch him pounce when so-called friends try to sneak a beer. This cat is great fun at parties.', 'olie.jpg','olie-s.jpg', '307.90',14);
INSERT INTO item VALUES(15, 1, 'Sophisticated Cat', 'This cat is from Paris. It has a very distinguished history and is looking for a castle to play in. This sophisticated cat has class and taste. No chasing on string, no catnip habits. Only the habits of royalty in this cats blood.', 'paris.jpg','paris-s.jpg', '307',15);
INSERT INTO item VALUES(16, 1, 'Princess cat', 'Just beauty and elegance. She will charm you from the moment she enters the room.', 'princess.jpg','princess-s.jpg', '307',16);
INSERT INTO item VALUES(17, 2, 'Lazy cat', 'Wow! This cat is cool. It has a beautiful tan coat. I wish I could get a sun tan of that color.', 'simba.jpg','simba-s.jpg', '307',17);
INSERT INTO item VALUES(18, 2, 'Scapper male cat', 'A scappy cat that likes to cause trouble. If you are looking for a challenge to your cat training skills, this scapper is the test!', 'thaicat.jpg','thaicat-s.jpg', '307',18);
INSERT INTO item VALUES(19, 1, 'Lazy cat', 'Buy me please. I love to sleep.', 'cat1.gif','cat1.gif', '307.19',19);
INSERT INTO item VALUES(20, 1, 'Old Cat', 'A great old pet retired from duty in the circus. This fully-trained tiger is looking for a place to retire. Loves to roam free and loves to eat other animals.', 'cat2.gif','cat2.gif', '200',20);
INSERT INTO item VALUES(21, 1, 'Young Cat', 'A great young pet to chase around. Loves to play with a ball of string.', 'cat3.gif','cat3.gif', '350',21);
INSERT INTO item VALUES(22, 1, 'Scrappy Cat', 'A real trouble-maker in the neighborhood. Looking for some T.L.C', 'cat4.gif','cat4.gif', '417',22);
INSERT INTO item VALUES(23, 1, 'Alley Cat', 'Loves to play in the alley outside my apartment, but looking for a warmer and safer place to spend its nights.', 'cat5.gif','cat5.gif', '307', 23);
INSERT INTO item VALUES(24, 2, 'Playful Cat', 'Come play with me. I am looking for fun.', 'cat7.gif','cat7.gif', '190', 24);
INSERT INTO item VALUES(25, 2, 'Long Haired Cat', 'Buy this fancy cat.', 'cat8.gif', 'cat8.gif', '199',25);
INSERT INTO item VALUES(26, 2, 'Fresh Cat', 'Just need a nice bath and i will be fresh as a kitten.', 'cat9.gif','cat9.gif', '303',26);
INSERT INTO item VALUES(27, 2, 'Wild Cat', 'This wild tiger loves to play.', 'cat10.gif', 'cat10.gif', '527',27);
INSERT INTO item VALUES(28, 2, 'Saber Cat', 'Buy me', 'cat11.gif', 'cat11.gif', '237',28);
INSERT INTO item VALUES(29, 2, 'Snappy Cat', 'Buy Me.', 'cat12.gif', 'cat12.gif', '337',29);

INSERT INTO item VALUES(100, 4, 'Pretty Dog', 'Just like a statue or work of art she is so pretty. If you are an artist this dog is a perfect subject for your portraits. She is such a poser! She belongs in Hollywood with her own star on the wlak of fame.', 'biscuit.jpg','biscuit-s.jpg', '100',30);
INSERT INTO item VALUES(101, 3, 'Beach Dog', 'A great dog to lay in the Sun with, chases a frisbee like a champ, and can swim like a shark. Heck, he can ride a surfboard if you dont mind sharing.', 'harrison.jpg','harrison-s.jpg', '250',31);
INSERT INTO item VALUES(102, 3, 'Scrapper Dog', 'This scrapy woofer needs some clean up and some lessons. Help him out and take him home.', 'honey.jpg','honey-s.jpg', '257',32);
INSERT INTO item VALUES(103, 3, 'Intense Worker Dog', 'My dog is full on energy and intensity. He needs a constant challenge to keep him happy. Great for a farm or house in the country. Bring him to work with you.', 'hunter.jpg','hunter-s.jpg', '500',33);
INSERT INTO item VALUES(104, 4, 'Old Dog', 'This old geezer just wants to sleep at your feet. Slip him some food under the table and watch him wag his tail. Great dog for someone who retired before the dot-com bubble burst. A partner to loaf with.', 'jack.jpg','jack-s.jpg', '215',34);
INSERT INTO item VALUES(105, 3, 'Female Huskie', 'Live in a cold area? This dog has her own winter jacket. She loves the snow. She even loves to eat the snow. One time, I saw her eat a whole snowman ...it was crazy fun to watch.', 'lady.jpg','lady-s.jpg', '2000',35);
INSERT INTO item VALUES(106, 3, 'Rasta Dog', 'Hey mon! Want a dog to chill with. This dog wants the rasta life. You can see it in her eyes. The ocean, the sunshine, all you need is this dog to be complete.', 'maggie.jpg','maggie-s.jpg', '400',36);
INSERT INTO item VALUES(107, 3, 'Tough Dog', 'Need a guard dog? An excellent guard dog that would love to sit next to you and watch reruns of police shows on TV. And loves kids. Its like having a trusted babysitter to watch your kids and home.', 'marianna.jpg','marianna-s.jpg', '500',37);
INSERT INTO item VALUES(108, 4, 'Sad Puppy', 'Help to put a smile on the face of this puppy. What? You did not know dogs could smile? Well, see what happens when you drop some steak on the floor!', 'puppup.jpg','puppup-s.jpg', '99',38);
INSERT INTO item VALUES(109, 3, 'Speedy Dog', 'A great runner for a fast owner. This is the fastest dog I have ever seen. Its naturally fast and does not take steroids to improve its performance.', 'rita.jpg','rita-s.jpg', '25',39);
INSERT INTO item VALUES(110, 4, 'Young Dog', 'Ready to play! This young dog is eager to learn new tricks. Bring me home.', 'sabrina.jpg','sabrina-s.jpg', '79',40);
INSERT INTO item VALUES(111, 3, 'Sleepy Dog', 'This sleepy dog is perfect for a relaxed home. Plus she just seems to love old music. Anytime she is near an elevator, she just wags her tail waiting to hear that old music.', 'thaidog.jpg','thaidog-s.jpg', '1000',41);
INSERT INTO item VALUES(112, 3, 'Lazy Dog', 'OK, I can not get this dog to fetch the newspaper. Not even for a doggy biscuit. His name is "Rhymes". He is so lazy, it drives me crazy.', 'dog1.gif','dog1.gif', '200',42);
INSERT INTO item VALUES(113, 3, 'Old Dog', 'This old geezer just wants to sleep at your feet. Slip him some food under the table and watch him wag his tail.', 'dog2.gif','dog2.gif', '70',43);
INSERT INTO item VALUES(114, 3, 'Young Dog', 'A great young pet in need of training.', 'dog3.gif','dog3.gif', '301',44);
INSERT INTO item VALUES(115, 4, 'Scrapper Dog', 'This scrapy woofer needs some clean up and some lessons. Help him out and take him home.', 'dog4.gif','dog4.gif', '410',45);
INSERT INTO item VALUES(116, 4, 'Grey Hound', 'A great runner for a fast owner. This is the fastest dog I have ever seen. Its naturally fast and does not take steroids to improve its performance.', 'dog5.gif', 'dog5.gif', '200',46);
INSERT INTO item VALUES(117, 4, 'Beach Dog', 'A great dog to lay in the Sun with, chases a frisbee like a champ, and can swim like a shark. Heck, he can ride a surfboard if you dont mind sharing.', 'dog6.gif', 'dog6.gif', '200',47);

INSERT INTO item VALUES(201, '5', 'Sweet Parrot', 'This young little parrot is a great pet. It loves to sit on your finger, so you can where it just like a ring, but so much more exotic than a simple diamond.', 'parrot-popcorn.jpg', 'parrot-popcorn-s.jpg', '250',48);
INSERT INTO item VALUES(202, '5', 'Female Eclectus Parrot', 'This bird really loves apples. She is a great companion and is quite beautiful. Just look at those colors. The read and blue makes her look like superman! And she can fly too!', 'eclectus-female-med.jpg', 'eclectus-female-thumb.jpg', '250',49);
INSERT INTO item VALUES(203, '5', 'Galah Parrot', 'My Galah Parrot needs a new home. This new home should have lots of trees and absolutely NO cats. I dont say that because the cats might eat the bird. Rather, I say this Galah needs to stay away from cats so that the cats don''t get hurt. This Galah loves the taste of cat meat.', 'galah-parrot-med.jpg', 'galah-parrot-thumb.jpg', '900',50);
INSERT INTO item VALUES(204, '6', 'Kookaburra Bird', 'Little Kookaburra for sale. This Kookaburra is tame and likes to just sit to tree branches. A very easy to care for pet.', 'kookaburra-med.jpg', 'kookaburra-thumb.jpg', '100',51);
INSERT INTO item VALUES(205, '6', 'Orange LoveBird', 'Can this love bird bring you some love? I believe this orange lovebird will bring good luck to your life, and for just a small fee it can be yours.', 'lovebird-med.jpg', 'lovebird-thumb.jpg', '75',52);
INSERT INTO item VALUES(206, '6', 'Blue Peacock', 'Buy a blue peacock and be the envy of all your neighbors! Guaranteed to turn heads. You will meet everyone in your neighborhood as they come by to see this beauty.', 'peacock-blue-med.jpg', 'peacock-blue-thumb.jpg', '55',53);
INSERT INTO item VALUES(207, '6', 'Wild Peacock', 'OK, this peacock is wild. And I dont mean that it is not tame. I mean this thing really likes to party! Its the best dancer I have ever seen. Trust me on this.', 'peacock-med.jpg', 'peacock-thumb.jpg', '65',54);
INSERT INTO item VALUES(208, '6', 'White Peacock', 'My white peacock is like a work of art. Its beauty is refreshing. Consider this an investment, just like buying a painting.', 'peakcock-white-med.jpg', 'peacock-white-thumb.jpg', '195',55);
INSERT INTO item VALUES(209, '6', 'Rainbow Lorikeet', 'Color! Color! Color! The rainbow lorikeet is a beautiful bird.', 'rainbow-lorikeet-med.jpg', 'rainbow-lorikeet-thumb.jpg', '299',56);
INSERT INTO item VALUES(210, '6', 'Stone Eagle', 'My eagle is really tough. He is like a rock! In fact he is made of stone. Ok, he may not have a heart and maybe he can not fly, but he needs no food and is so easy to care for. And he has a great temperament, and in fact we have never had any disagreements at all.', 'eagle-stone-med.jpg', 'eagle-stone-thumb.jpg', '800',57);
INSERT INTO item VALUES(211, '6', 'Red Macaw', 'Look at this majestic beauty! The colors of this macaw are breath-taking. In fact, the colors are so strong you should make sure the furniture in your home matches this bird, as this will be the center-piece of your lovely home.', 'macaw.jpg', 'macaw-thumb.jpg', '1800',58);
INSERT INTO item VALUES(212, '5', 'Squaky Bird', 'A great noisy bird to drive your parents crazy. If you think playing a stereo loud will test your parents patience, wait till you try this screeching bird. It will fly around and cause a commotion that will have you laughing for days. This is a sure-fire way to get your parents to get you your own apartment.', 'CIMG9127.jpg', 'CIMG9127-s.jpg', '303',59);
INSERT INTO item VALUES(213, '5', 'Pink Bird', 'A beautiful pink bird. Its not white. Its not red. Its pink.', 'CIMG9104.jpg', 'CIMG9104-s.jpg', '3003',60);
INSERT INTO item VALUES(214, '6', 'Wild Bird', 'A wild bird. This little rebel thinks its not a house pet, but really it is as tame as a lap dog. It just looks so wild and impresses your friends. Owning this wild bird is cooler than getting a tatoo and hurts less!', 'CIMG9109.jpg', 'CIMG9109-s.jpg', '527',61);
INSERT INTO item VALUES(215, '6', 'Really Wild Bird', 'A really wild pet. This thing is like a zombie in a horror movie it is so wild. I tried for 2 years to tame this beast and all that happened was I got peck marks all over my hands. Maybe you are a former circus trainer looking for a challenge? If so this birdy needs a home.', 'CIMG9109.jpg', 'CIMG9109-s.jpg', '527',62);
INSERT INTO item VALUES(216, '6', 'Crazy Bird', 'This crazy bird once flew over the cukoos nest. Some people say that I am crazy. Well, you should meet my bird if you want to meet crazy. This bird is plain looney.', 'CIMG9109.jpg', 'CIMG9109-s.jpg', '527',63);
INSERT INTO item VALUES(217, '6', 'Smart Bird', 'A great smart pet. Perfect for an engineer. This pet is so smart it can do your coding for you. Just dont tell your boss!', 'CIMG9084.jpg','CIMG9084-s.jpg', '527',64);
INSERT INTO item VALUES(218, '6', 'Funny Bird', 'A great funny pet. Ever hear the joke about a bird, a hacker, and Java book? Well, this bird can tell it to you. It will make you laugh until you cry. Plus, it can teach you jokes which you can use at parties to impress your friends. That is right sir, this bird will make even you funny!', 'CIMG9109.jpg','CIMG9109-s.jpg', '527',65);
INSERT INTO item VALUES(219, '6', 'Active Bird', 'A great active pet. This bird once flew a marathon with me and did not get tired. It can fly along while you jog on the trails. Just make sure the hawks do not swoop down and eat it.', 'CIMG9088.jpg','CIMG9088-s.jpg', '527',66);
INSERT INTO item VALUES(220, '6', 'Curious Bird', 'A great curious birdy. Everything will pique this birds interest. It will investigate every new thing it sees.', 'CIMG9138.jpg','CIMG9109-s.jpg', '1527',67);
INSERT INTO item VALUES(221, '6', 'Itchy Bird', 'This bird needs some attention. I think he has dandruff in his feathers. He is always scratching so I knick-named him "Itchy". I wish I had claws like that to scratch MY head. Maybe if you are a good trainer, you can teach him to scratch your back nicely with those talons.', 'CIMG9129.jpg', 'CIMG9129-s.jpg', '200',68);

INSERT INTO item VALUES(301, '8', 'Spotted JellyFish', 'Buy Me.', 'spotted-jellyfish-med.jpg','spotted-jellyfish-thumb.jpg', '55',69);
INSERT INTO item VALUES(302, '7', 'Small Sea Nettle JellyFish', 'Buy Me.', 'sea-nettle-jellyfish-med.jpg','sea-nettle-jellyfish-thumb.jpg', '75',70);
INSERT INTO item VALUES(303, '8', 'RockFish', 'Buy Me.', 'rockfish-med.jpg','rockfish-thumb.jpg', '125',71);
INSERT INTO item VALUES(304, '8', 'Purple JellyFish', 'Buy Me.', 'purple-jellyfish-med.jpg','purple-jellyfish-thumb.jpg', '225',72);
INSERT INTO item VALUES(305, '8', 'White Octopus', 'Buy Me.', 'octopus-white-med.jpg','octopus-white-thumb.jpg', '2000',73);
INSERT INTO item VALUES(306, '8', 'Red Octopus', 'Buy Me.', 'octopus-red-med.jpg','octopus-red-thumb.jpg', '2000',74);
INSERT INTO item VALUES(307, '8', 'Koi', 'I love this fish. But my new roommate has a cat, and the cat just so does not seem to get along with the  fish. At first, I thought the cat was trying to just play with the fish, or maybe learn to swim. And then I remembered that show where they explained that Tuna is the chicken of the sea, but that does not mean its chicken. Not, its fish. And cats love tuna, so then I got scared and realized my fish is in danger. Help me! Buy my little chicken of the sea.', 'koi-med.jpg','koi-thumb.jpg', '150',75);
INSERT INTO item VALUES(308, '8', 'GlassFish', 'FREE! And open source! The best open source Java EE 5 application server project.', 'glassfish-colored-med.jpg','glassfish-colored-thumb.jpg', '10',76);
INSERT INTO item VALUES(309, '7', 'CuttleFish', 'This rare and spooky looking fish is the talk of town where I live!', 'cuttlefish-med.jpg','cuttlefish-thumb.jpg', '900',77);
INSERT INTO item VALUES(310, '7', 'Silver Carp Car', 'You will never believe the fish I caught! This is the ultimate fishing stories... seriously! I pulled this baby out of the forbidden lake behind the nuclear power plant.', 'carp-car-med.jpg','carp-car-thumb.jpg', '500',78);
INSERT INTO item VALUES(311, '8', 'Moon JellyFish', 'Buy Me.', 'moon-jelly-med.jpg','moon-jelly-thumb.jpg', '1225',79);
INSERT INTO item VALUES(312, '7', 'Sea Anemone', 'This colorful sea anemone will really brighten up your aquarium.', 'sea-anemone-med.jpg','sea-anemone-thumb.jpg', '112',80);
INSERT INTO item VALUES(313, '7', 'Gold Fish', 'Perfect for a first fish for a child. This fish is not good in the same tank as some bigger fish as they see this little gold fish as a snack.', 'fish2.gif','fish2.gif', '2',81);
INSERT INTO item VALUES(314, '7', 'Angel Fish', 'I love this fish. But my new roommate has a cat, and the cat just so does not seem to get along with the  fish. At first, I thought the cat was trying to just play with the fish, or maybe learn to swim. And then I remembered that show where they explained that Tuna is the chicken of the sea, but that does not mean its chicken. Not, its fish. And cats love tuna, so then I got scared and realized my fish is in danger. Help me! Buy my little chicken of the sea.', 'fish3.gif','fish3.gif', '100',82);
INSERT INTO item VALUES(315, '8', 'Striped Tropical', 'This large fish needs plenty of room to swim. Either a big aquarium or your bath tub.', 'fish4.gif','fish4.gif', '20.85',83);

INSERT INTO item VALUES(401, '10', 'Hawaiian Green Gecko', 'Oloha Wahine! I am from Hawaii. I like to hang out on leaves and just kick back at the beach. You know you can not resist my shiny green skin. So what do you say, want to come visit me?', 'hawaiian-lizard-med.jpg','hawaiian-lizard-thumb.jpg', '110',84);
INSERT INTO item VALUES(402, '10', 'African Spurred Tortoise', 'Many people get this as a pet, not realizing it will grow over time to be as large as an ottoman. I must confess, that I am one of those people. Which is why I am now trying to sell this tortoise -it is getting big and starting to scare me. Only homes with lots of room please. ', 'african-spurred-tortoise.jpg','african-spurred-tortoise-thumb.jpg', '300',85);
INSERT INTO item VALUES(403, '10', 'Small Box Turtle', 'Cute little turtle for sale. This box turtle is easy to care for and just needs a good home.', 'box-turtle.jpg','box-turtle-thumb.jpg', '5000',86);
INSERT INTO item VALUES(404, '10', 'Mexican Redkneed Tarantula', 'No, I am not reptile food! But if you like exotic reptiles, well maybe you would like me too? I promise not to bite you and turn you into spiderman, unless that is what you want. Perhaps you want to climb walls with me?', 'mexican-redkneed-tarantula.jpg','mexican-redkneed-tarantula-thumb.jpg', '530',87);
INSERT INTO item VALUES(405, '10', 'Large Box Turtle', 'Turtles are very nice creatures. This large Box Turtle is super fast. Well, compared to other turtles in the neighborhood, this is the Cheetah of turtles.', 'box-turtle2.jpg','box-turtle2-thumb.jpg', '5000',88);
INSERT INTO item VALUES(406, '10', 'California Desert Tortoise', 'An endangered tortoise. We are lookng for a zoo to house this beautiful creature. It has a quite nice disposition and will let children pet its shell to discover the mysteries of biology.', 'california-desert-tortoise.jpg','california-desert-tortoise-thumb.jpg', '599',89);
INSERT INTO item VALUES(407, '9', 'Florida King Snake', 'This Florida King Snake is really nice. It may look spooky and scare your friends at first, but eventually they will learn to love this slippery snake.', 'florida-king-snake.jpg','florida-king-snake-thumb.jpg', '150',90);
INSERT INTO item VALUES(408, '9', 'Leopard Gecko', 'My little leopard gecko needs a new home. If you are interested in purchasing this gecko, I advise you to hurry as it is bound to sell fast.', 'leopard-gecko.jpg','leopard-gecko-thumb.jpg', '700',91);
INSERT INTO item VALUES(409, '10', 'Prehensile Tailed Skink', 'Long Tailed Prehensile Tailed Skink for sale. This is the longest tail I have ever seen on a lizard. It goes on for days. Notice the gloves? Be aware of the sharp claws when holding this lizard, even though she is very nice.', 'prehensile-tailed-skink.jpg','prehensile-tailed-skink-thumb.jpg', '800',92);
INSERT INTO item VALUES(410, '10', 'African Spurred Tortoise', 'This is not a turtle, it is an African Spurred Tortoise. It grows to get really large. Maybe you have a big yard and can really care for this tortoise? If so, now is your chance to buy it.', 'african-spurred-tortoise2.jpg','african-spurred-tortoise2-thumb.jpg', '125',93);
INSERT INTO item VALUES(411, '10', 'Box Turtle', 'This box turtle needs a new home. I am moving and unfortunately must sell all my animals.', 'box-turtle3.jpg','box-turtle3-thumb.jpg', '900',94);
INSERT INTO item VALUES(412, '9', 'Prehensile Tailed Skink', 'My lizard is really cool and all my school friends are really impressed when I bring it to show-and-tell. But, the other day at home, he escaped from his cage and it REALLY freaked my mom out. Now she is mad at me. Sigh, since I can not sell my Mom, please buy my lizard.', 'prehensile-tailed-skink2.jpg','prehensile-tailed-skink2-thumb.jpg', '5000',95);
INSERT INTO item VALUES(413, '9', 'Leopard Gecko', 'Are you seeing spots in front of your eyes? No you are not sleepy. That is just the affect of my gecko and his beautiful spotted skin.', 'leopard-gecko2.jpg','leopard-gecko-thumb2.jpg', '1200',96);
INSERT INTO item VALUES(414, '9', 'Guinea Pig', 'Oh no! I dont like snakes at all. Why am I here in this list surrounded by serpents? Help save me! Take me home to a nice warm cage with fresh newspaper. ', 'guinea-pig.jpg','guinea-pig-thumb.jpg', '1000',97);
INSERT INTO item VALUES(415, '9', 'Iron Dragon', 'Just look at this fierce beast. This is so tough its like a secret military weapon.', 'dragon-iron-med.jpg','dragon-iron-thumb.jpg', '500',98);
INSERT INTO item VALUES(416, '9', 'Green slider', 'This snake looks like a lizard! It must be rare since I have never seen one like it.', 'lizard3.gif','lizard3.gif','10',99);
INSERT INTO item VALUES(417, '10', 'Green Iguana', 'This is one proud and tough lizard. He holds his head high in any circumstance.', 'lizard1.gif','lizard1.gif', '2100',100);
INSERT INTO item VALUES(418, '10', 'Iguana', 'My iguana needs a home. His tail is really long. He is very nice.', 'lizard2.gif','lizard2.gif', '500',101);
INSERT INTO item VALUES(419, '10', 'Frog', 'This little green frog was rescued from the chef in the kitchen of a french restaurant. If I had not acted quickly, his legs would have been appetizers. Now for just a small fee you can buy him as your pet.', 'frog1.gif','frog1.gif', '1500',102);



