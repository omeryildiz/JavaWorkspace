
drop procedure if exists update_sequences;

//
CREATE PROCEDURE update_sequences()

begin 
declare l_loop_end INT default 0;
declare tb_name VARCHAR(255);
declare name_cursor cursor for select table_name from information_schema.TABLES where 
                                                      table_schema = 'tekir' and 
                                                      table_name != 'hibernate_sequences' and
                                                      table_name not like 'ACT_%';

declare continue handler for sqlstate '02000' set l_loop_end = 1;
   open name_cursor;

   repeat
       fetch name_cursor into tb_name;

       if not l_loop_end then

           set @update_query = CONCAT('update hibernate_sequences set sequence_next_hi_value = (select convert(max(id)/50,decimal(20))+1 from tekir.', tb_name, ') where sequence_name="', tb_name,'"'); 

           PREPARE ctmp FROM @update_query;
           EXECUTE ctmp;
           DEALLOCATE PREPARE ctmp;
       end if;

    until l_loop_end end repeat;

    close name_cursor;

end;
//

call update_sequences();
