package the.bytecode.club.bytecodeviewer.util;

/**
 * @author Hupan
 * @since 11/20/2019
 */
class SeqAndCount
{
	Integer seq;
	Integer count;
	
	public static SeqAndCount init()
	{
		SeqAndCount seqAndCount = new SeqAndCount();
		seqAndCount.setSeq(1);
		seqAndCount.setCount(1);
		return seqAndCount;
	}
	
	public SeqAndCount incrSeq()
	{
		seq = seq + 1;
		return this;
	}
	
	public SeqAndCount incrCount()
	{
		count = count + 1;
		return this;
	}
	
	public SeqAndCount decrCount()
	{
		count = count - 1;
		return this;
	}
	
	public SeqAndCount incrSeqAndCount()
	{
		seq = seq + 1;
		count = count + 1;
		return this;
	}
	
	public Integer getSeq()
	{
		return seq;
	}
	
	public void setSeq(Integer seq)
	{
		this.seq = seq;
	}
	
	public Integer getCount()
	{
		return count;
	}
	
	public void setCount(Integer count)
	{
		this.count = count;
	}
}
