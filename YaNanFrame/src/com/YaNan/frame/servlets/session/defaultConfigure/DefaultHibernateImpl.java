package com.YaNan.frame.servlets.session.defaultConfigure;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.YaNan.frame.hibernate.database.Delete;
import com.YaNan.frame.hibernate.database.Insert;
import com.YaNan.frame.hibernate.database.Query;
import com.YaNan.frame.servlets.session.entity.TokenCell;
import com.YaNan.frame.servlets.session.interfaceSupport.TokenHibernateInterface;
 
//@Register
public class DefaultHibernateImpl implements TokenHibernateInterface{
	
	@Override
	public boolean containerToken(String tokenId) {
		Query query = new Query(TokenCellEntity.class);
		query.addCondition("tokenId",tokenId);
		try {
			return query.query().size()==1;
		} catch (SecurityException
				| IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void addToken(TokenCell tokenCell) {
		TokenCellEntity tce = new TokenCellEntity(tokenCell);
		Insert insert = new Insert(tce);
		insert.insert();
	}

	@Override
	public TokenCell getToken(String tokenId) {
		Query query = new Query(TokenCellEntity.class);
		query.addCondition("tokenId", tokenId);
		try {
			TokenCellEntity tce = (TokenCellEntity) query.query().get(0);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date;
			date= sdf.parse(tce.getDate());
			TokenCell te = new TokenCell(tce.getTokenId(),date,tce.getOther());
			return te;
		} catch (SecurityException
				| IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void destory(String tokenId) {
		Delete delete = new Delete(TokenCellEntity.class);
		delete.addCondition("TokenId",tokenId);
		delete.delete();
	}

	@Override
	public Object get(String tokenId, String key) {
		Query query = new Query(TokenDataEntity.class);
		query.addCondition("TokenId", tokenId);
		query.addCondition("tKey",key);
		try {
			List<?> list=query.query();
			if(list.size()!=0){
				TokenDataEntity tde = (TokenDataEntity) list.get(0);
				return tde.getTValue();
			}
		} catch (SecurityException
				| IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void set(String tokenId, String key, Object value) {
		TokenDataEntity tde = new TokenDataEntity();
		tde.setTKey(key);
		tde.setTokenId(tokenId);
		tde.setTValue(value.toString());
		Insert insert = new Insert(tde);
		insert.insertOrUpdate("TokenId","tKey");
	}

	@Override
	public void clear(String tokenId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(String tokenId, String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addRole(String tokenId, String... role) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearRole(String tokenId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeRole(String tokenId, String role) {
		// TODO Auto-generated method stub
		
	}

}
