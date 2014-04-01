package NodeDomain;

public abstract class ADomainData<T extends LogicalNode> {

	public abstract void onModify(int ModifyKey, float value);

	public abstract void OnRefresh(float fCurX, float fCurY, float fRadius,
			float fLen, float fAngle);

}
