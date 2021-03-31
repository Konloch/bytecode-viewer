_handle_types = 'getField getStatic putField putStatic invokeVirtual invokeStatic invokeSpecial newInvokeSpecial invokeInterface'.split()
handle_codes = dict(zip(_handle_types, range(1,10)))
handle_rcodes = {v:k for k,v in handle_codes.items()}

newarr_rcodes = ([None]*4) + 'boolean char float double byte short int long'.split()
newarr_codes = dict(zip('boolean char float double byte short int long'.split(), range(4,12)))

vt_rcodes = ['Top','Integer','Float','Double','Long','Null','UninitializedThis','Object','Uninitialized']
vt_codes = {k:i for i,k in enumerate(vt_rcodes)}

et_rtags = dict(zip(map(ord, 'BCDFIJSZsec@['), 'byte char double float int long short boolean string enum class annotation array'.split()))
et_tags = {v:k for k,v in et_rtags.items()}
